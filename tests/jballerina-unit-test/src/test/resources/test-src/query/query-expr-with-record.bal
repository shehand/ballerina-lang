public function testRecordBasedQueryExpr() {
    testQueryExprForNilFieldType();
    testQueryExprForOptionalField();
    testQueryExprForOptionalFieldV2();
    testMethodParamWithLet();
    testQueryExprWithOpenRecord();
    testQueryExprInLambda();
    testQueryExprInLambdaV2();
    testQueryExprInLambdaV3();
}

type Person record {
    string firstName;
    string lastName;
    int age;
    string? address;
};

type Customer record {
    string firstName;
    string lastName;
    int age;
    string address?;
};

type Employee record {
    string firstName;
    string lastName;
    int age;
    string? address?;
};

public function testQueryExprForNilFieldType() {

    Person p1 = {firstName: "Alex", lastName: "George", age: 23, address: ()};
    Person p2 = {firstName: "Ranjan", lastName: "Fonseka", age: 30, address: "Kandy"};
    Person p3 = {firstName: "John", lastName: "David", age: 33, address: ()};

    Person[] personList = [p1, p2, p3];

    Person[] outputPersonList =
            from var person in personList
            select {
                   firstName: person.firstName,
                   lastName: person.lastName,
                   age: person.age,
                   address: person.address
             };

    string? ad = outputPersonList[0]?.address;
    assertEquality((), ad);

    assertEquality("Kandy", outputPersonList[1]?.address);
}

public function testQueryExprForOptionalField() {

    Customer p1 = {firstName: "Alex", lastName: "George", age: 23, address: "Colombo"};
    Customer p2 = {firstName: "Ranjan", lastName: "Fonseka", age: 30};
    Customer p3 = {firstName: "John", lastName: "David", age: 33};

    Customer[] personList = [p1, p2, p3];

    Person[] outputPersonList =
            from var person in personList
            select {
                   firstName: person.firstName,
                   lastName: person.lastName,
                   age: person.age,
                   address: person["address"]
             };

    assertEquality("Colombo", outputPersonList[0]?.address);
    assertEquality((), outputPersonList[1]?.address);
}

public function testQueryExprForOptionalFieldV2() {

    Employee p1 = {firstName: "Alex", lastName: "George", age: 23, address: "Colombo"};
    Employee p2 = {firstName: "Ranjan", lastName: "Fonseka", age: 30};
    Employee p3 = {firstName: "John", lastName: "David", age: 33};

    Employee[] employeeList = [p1, p2, p3];

    Person[] outputPersonList =
            from var {firstName, lastName, age, address} in employeeList
            select {
                   firstName: firstName.toString(),
                   lastName: lastName.toString(),
                   age: age,
                   address: address
             };

    assertEquality("Colombo", outputPersonList[0]?.address);
    assertEquality((), outputPersonList[1]?.address);
}

public function testMethodParamWithLet() {

    Person p1 = {firstName: "Alex", lastName: "George", age: 23, address: ()};
    Person p2 = {firstName: "Ranjan", lastName: "Fonseka", age: 30, address: "Kandy"};
    Person p3 = {firstName: "John", lastName: "David", age: 33, address: ()};

    Person[] personList = [p1, p2, p3];

    Person[] outputPersonList =
            from var person in personList
            let int age = 35
            select {
                   firstName: person.firstName,
                   lastName: person.lastName,
                   age: age,
                   address: person.address
             };

    int age = 5;

    assertEquality(outputPersonList[0].age, 35);
    assertEquality(age, 5);
}

public function testQueryExprWithOpenRecord() {

    Person p1 = {firstName: "Alex", lastName: "George", age: 23, address: (), "zipCode": "2000"};
    Person p2 = {firstName: "Ranjan", lastName: "Fonseka", age: 30, address: "Kandy", "zipCode": "0400"};
    Person p3 = {firstName: "John", lastName: "David", age: 33, address: (), "zipCode": "0655"};

    Person[] personList = [p1, p2, p3];

    Person[] outputPersonList =
            from var person in personList
            select {
                   firstName: person.firstName,
                   lastName: person.lastName,
                   age: person.age,
                   address: person.address,
                   "newZipCode": person["zipCode"]
             };

    string? ad = outputPersonList[0]?.address;
    assertEquality((), ad);

    assertEquality("Kandy", outputPersonList[1]?.address);
    assertEquality("0400", outputPersonList[1]["newZipCode"]);
}

public function testQueryExprInLambda() {

    function () returns Person[] anonFunction =
            function () returns Person[] {

                Person p1 = {firstName: "Alex", lastName: "George", age: 23, address: ()};
                Person p2 = {firstName: "Ranjan", lastName: "Fonseka", age: 30, address: "Kandy"};
                Person p3 = {firstName: "John", lastName: "David", age: 33, address: ()};

                Person[] personList = [p1, p2, p3];

                Person[] outputPersonList =
                        from var {firstName, lastName, age, address} in personList
                        select {
                               firstName: firstName,
                               lastName: lastName,
                               age: age,
                               address: address
                         };

                return outputPersonList;
    };

    assertEquality(23, anonFunction()[0].age);
}

public function testQueryExprInLambdaV2() {

    Person p1 = {firstName: "Alex", lastName: "George", age: 23, address: ()};
    Person p2 = {firstName: "Ranjan", lastName: "Fonseka", age: 30, address: "Kandy"};
    Person p3 = {firstName: "John", lastName: "David", age: 33, address: ()};

    Person[] personList = [p1, p2, p3];

    function () returns Person[] anonFunction =
            function () returns Person[] {

                Person[] outputPersonList =
                        from var {firstName, lastName, age, address} in personList
                        select {
                               firstName: firstName,
                               lastName: lastName,
                               age: age,
                               address: address
                         };

                return outputPersonList;
    };

    assertEquality(23, anonFunction()[0].age);
}

Person p1 = {firstName: "Alex", lastName: "George", age: 23, address: ()};
Person p2 = {firstName: "Ranjan", lastName: "Fonseka", age: 30, address: "Kandy"};
Person p3 = {firstName: "John", lastName: "David", age: 33, address: ()};

Person[] personList = [p1, p2, p3];

public function testQueryExprInLambdaV3() {

    function () returns Person[] anonFunction =
            function () returns Person[] {

                Person[] outputPersonList =
                        from var {firstName, lastName, age, address} in personList
                        select {
                               firstName: firstName,
                               lastName: lastName,
                               age: age,
                               address: address
                         };

                return outputPersonList;
    };

    assertEquality(23, anonFunction()[0].age);
}

//---------------------------------------------------------------------------------------------------------
type AssertionError error;

const ASSERTION_ERROR_REASON = "AssertionError";

function assertTrue(any|error actual) {
    assertEquality(true, actual);
}

function assertFalse(any|error actual) {
    assertEquality(false, actual);
}

function assertEquality(any|error expected, any|error actual) {
    if expected is anydata && actual is anydata && expected == actual {
        return;
    }

    if expected === actual {
        return;
    }

    panic AssertionError(ASSERTION_ERROR_REASON, message = "expected '" + expected.toString() + "', found '" + actual.toString () + "'");
}
