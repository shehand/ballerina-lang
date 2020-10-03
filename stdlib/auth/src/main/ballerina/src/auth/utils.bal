// Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/cache;
import ballerina/lang.'array as arrays;
import ballerina/lang.'string as strings;
import ballerina/log;
import ballerina/stringutils;

# Default charset to be used with password hashing.
public const string DEFAULT_CHARSET = "UTF-8";

# Prefix used to denote special configuration values.
public const string CONFIG_PREFIX = "@";

# Prefix used to denote that the config value is a SHA-256 hash.
public const string CONFIG_PREFIX_SHA256 = "@sha256:";

# Prefix used to denote that the config value is a SHA-384 hash.
public const string CONFIG_PREFIX_SHA384 = "@sha384:";

# Prefix used to denote that the config value is a SHA-512 hash.
public const string CONFIG_PREFIX_SHA512 = "@sha512:";

# Prefix used to denote Basic Authentication scheme.
public const string AUTH_SCHEME_BASIC = "Basic ";

# The prefix used to denote the Bearer Authentication scheme.
public const string AUTH_SCHEME_BEARER = "Bearer ";

# The table name specified in the user section of the TOML configuration.
const string CONFIG_USER_SECTION = "b7a.users";

# Extracts the username and the password from the base64-encoded `username:password` value.
# ```ballerina
# [string, string]|auth:Error [username, password] = auth:extractUsernameAndPassword("<credential>");
# ```
#
# + credential - Base64-encoded `username:password` value
# + return - A `string` tuple with the extracted username and password or else an `auth:Error` occurred while
#            extracting credentials
public function extractUsernameAndPassword(string credential) returns [string, string]|Error {
    byte[]|error result = arrays:fromBase64(credential);
    if (result is error) {
        return prepareError(result.message(), result);
    }

    string|error fromBytesResults = strings:fromBytes(<byte[]>result);
    if (fromBytesResults is string) {
        string[] decodedCredentials = stringutils:split(fromBytesResults, ":");
        if (decodedCredentials.length() != 2) {
            return prepareError("Incorrect credential format. Format should be username:password");
        } else {
            return [decodedCredentials[0], decodedCredentials[1]];
        }
    } else {
        return prepareError(fromBytesResults.message(), fromBytesResults);
    }
}

# Checks whether the scopes of the user match the scopes of the resource.
#
# + resourceScopes - Scopes of the resource
# + userScopes - Scopes of the user
# + authzCacheKey - Authorization cache key
# + positiveAuthzCache - The `cache:Cache` for positive authorizations
# + negativeAuthzCache - The `cache:Cache` for negative authorizations
# + return - `true` if there is a match between the resource and user scopes or else `false` otherwise
public function checkForScopeMatch(string[]|string[][] resourceScopes, string[] userScopes, string authzCacheKey,
                                   cache:Cache? positiveAuthzCache, cache:Cache? negativeAuthzCache) returns boolean {
    boolean? authorizedFromCache = authorizeFromCache(authzCacheKey, positiveAuthzCache, negativeAuthzCache);
    if (authorizedFromCache is boolean) {
        return authorizedFromCache;
    }
    if (userScopes.length() > 0) {
        boolean authorized = true;
        if (resourceScopes is string[]) {
            authorized = matchScopes(resourceScopes, userScopes);
        } else {
            foreach string[] resourceScope in resourceScopes {
                authorized = authorized && matchScopes(resourceScope, userScopes);
            }
        }
        cacheAuthzResult(authorized, authzCacheKey, positiveAuthzCache, negativeAuthzCache);
        return authorized;
    }
    return false;
}

# Tries to retrieve an authorization decision from the cached information if any.
#
# + authzCacheKey - Cache key
# + positiveAuthzCache - Cache for positive authorizations
# + negativeAuthzCache - Cache for negative authorizations
# + return - `true` or `false` in case of a cache hit or else `()` in case of a cache miss
function authorizeFromCache(string authzCacheKey, cache:Cache? positiveAuthzCache,
                            cache:Cache? negativeAuthzCache) returns boolean? {
    cache:Cache? pCache = positiveAuthzCache;
    if (pCache is cache:Cache) {
        any|cache:Error positiveCacheResponse = pCache.get(authzCacheKey);
        if (positiveCacheResponse is boolean) {
            return true;
        }
    }

    cache:Cache? nCache = negativeAuthzCache;
    if (nCache is cache:Cache) {
        any|cache:Error negativeCacheResponse = nCache.get(authzCacheKey);
        if (negativeCacheResponse is boolean) {
            return false;
        }
    }
    return ();
}

# Caches the authorization result.
#
# + authorized - The `boolean` flag to indicate the authorization decision
# + authzCacheKey - Cache key
# + positiveAuthzCache - The `cache:Cache` for positive authorizations
# + negativeAuthzCache - The `cache:Cache` for negative authorizations
function cacheAuthzResult(boolean authorized, string authzCacheKey, cache:Cache? positiveAuthzCache,
                          cache:Cache? negativeAuthzCache) {
    if (authorized) {
        cache:Cache? pCache = positiveAuthzCache;
        if (pCache is cache:Cache) {
            cache:Error? result = pCache.put(authzCacheKey, authorized);
            if (result is cache:Error) {
                log:printDebug(function() returns string {
                    return "Failed to add entry to positive authz cache";
                });
                return;
            }
        }
    } else {
        cache:Cache? nCache = negativeAuthzCache;
        if (nCache is cache:Cache) {
            cache:Error? result = nCache.put(authzCacheKey, authorized);
            if (result is cache:Error) {
                log:printDebug(function() returns string {
                    return "Failed to add entry to negative authz cache";
                });
                return;
            }
         }
    }
}

# Tries to find a match between the two scope arrays.
#
# + resourceScopes - Scopes of the resource
# + userScopes - Scopes of the user
# + return - `true` if one of the resourceScopes can be found at `userScopes` or else `false` otherwise
function matchScopes(string[] resourceScopes, string[] userScopes) returns boolean {
    foreach string resourceScope in resourceScopes {
        foreach string userScope in userScopes {
            if (resourceScope == userScope) {
                return true;
            }
        }
    }
    return false;
}
