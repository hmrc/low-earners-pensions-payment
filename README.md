
# low-earners-pensions-payments

## Contents

---

1. [What is this service](#what-is-this-service)
2. [Using and testing the service](#using-and-testing-the-service)
   1. [Running the service](#running-the-service)
   2. [Integration and unit tests](#integration-and-unit-tests)
   3. [Test coverage](#test-coverage)
3. [Retrieve low earner's pension payment summary](#retrieve-lepp-summary)
   1. [Request details](#request-details)
   2. [Response details](#response-details)
4. [Retrieve low earner's pension payment details](#retrieve-lepp-details)
   1. [Request details](#request-details-1)
   2. [Response details](#response-details-1)
5. [Available test scenarios](#available-test-scenarios)
6. [License](#license)

<br/>

## What is this service?

---

This is a backend microservice for the Low Earners Pension Payment (LEPP) project. For the frontend component of the
LEPP project see the [low-earners-pensions-payment-frontend](https://github.com/hmrc/low-earners-pensions-payment-frontend) repository.

This microservice contains five endpoints:
- [Retrieve LEPP Summary](#retrieve-lepp-summary)
- [Retrieve LEPP Details](#retrieve-lepp-details)
- Accept LEPP Payment (TBC)
- Verify BARS (bank account reputation service) status (TBC)
- Update BARS Status (TBC)

For the `Retrieve LEPP Details`, `Retrieve LEPP Summary`, and `Accept LEPP Payment` endpoints, test data is provided via
the [lepp-nps-stub]() service for the local, and staging environments. This test data is purely static, and covers the
minimum range of error and success scenarios needed to complete acceptance testing of the project. The data is also not 
intended to be 100% realistic, i.e the `basicRatePercentage` returned in a given scenario may not represent an actual
tax band that someone can be in. Each scenario may be triggered through use of certain `NINO` (National Insurance Number)
prefixes. For example, using any `NINO` beginning with `AA1`will result in a default success scenario being returned.
This is further detailed in the [Available test scenarios](#available-test-scenarios) section below.

The `Verify BARS`, and `Update BARS Status` endpoints are used to manage a lockout feature implemented onto `LEPP` to
prevent users from accessing the service if they make too many failed requests to submit valid bank details in a certain
period of time. No stubbed data is required to access these endpoints as they do not make any external calls from this
microservice to any other.

<br/>

## Using and testing the service

---

### Running the service

To run the service locally use the command:

```shell
sbt run
```

You can use service manage to run all dependent microservices using the command below
```shell
sm2 --start LEPP_ALL
```
To stop services:
```shell
sm2 --stop LEPP_ALL
```

### Using the endpoints

When run locally, the service appear at port `7504`

Each endpoint is protected with certain authorisation checks. To access the endpoints you must make a request containing
a valid `Authorization` header for a user: 
- with above 250CL
- which is signed up to `PTA` (Personal Tax Account)
- which is associated with a `NINO`

If any of these conditions is not met then a request to the service will not succeed. We also do not provide support for
any user types which aim to file, or view data on behalf of another individual, i.e agents, capacitors, trusted helpers, etc 

### Integration and unit tests

To run unit tests:
```shell
sbt test
```
To run Integration tests:
```shell
sbt it/test
```

### Test coverage

To check test coverage:

```shell
sbt clean coverage test it/test coverageReport
```

or use the shortcut command
```shell
sbt testc
```

<br/>

## Retrieve LEPP Summary

---

### Request details

#### Request Url:

~~~
GET /low-earners-pensions-payment/get-lepp-summary
~~~

#### Request headers:

| <div style="width:120px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:150px">Format</div> | <div style="width:270px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|---------------------------------------|----------------------------------------|
| correlationId                        | Header                             | Yes                                     | [**See here**](#common-formats)       | `e470d658-99f7-4292-a4a1-ed12c72f1337` |
| authorization                        | Header                             | Yes                                     | OAuth 2.0 Bearer token                | `Bearer XXXXXXXXXXXXX`                 |


#### Request body:

`No request body`

<br/>

### Response details

#### Response headers:

| <div style="width:120px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:150px">Format</div> | <div style="width:270px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|---------------------------------------|----------------------------------------|
| correlationId                        | Header                             | Yes                                     | [**See here**](#common-formats)       | `e470d658-99f7-4292-a4a1-ed12c72f1337` |

#### Error responses:
This API may return the following error statuses:
- [400 - Bad Request](#service-errors)
- [500 - Internal Server Error](#service-errors)

#### 200 OK - Response body:

`application/json;charset=UTF-8`

| <div style="width:200px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:200px">Format</div>                                                                | <div style="width:175px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|------------------------------------------------------------------------------------------------------|----------------------------------------|
| status                               | Body                               | Yes                                     | One of: <br/> -`"NOT_ELIGIBLE"`<br/> -`"NO_ACTIONS"` <br/> -`"CHECK"` <br/> -`"PAYMENTS_AVAILABLE"`  | `"CHECK"`                              |
| data                                 | Body                               | No                                      | `JS Object`                                                                                          | `*See example*`                        |

**`data` object definition:**
<a id="lepp-details"></a>

| <div style="width:200px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:200px">Format</div>                                                                | <div style="width:175px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|------------------------------------------------------------------------------------------------------|----------------------------------------|
| currentLowEarnersOptimisticLock      | Body                               | Yes                                     | `0 <= Integer <= 254`                                                                                | `132`                                  |
| identifier                           | Body                               | Yes                                     | `NINO` [**(See here)**](https://www.gov.uk/hmrc-internal-manuals/national-insurance-manual/nim39110) | `QQ123456A`                            |
| lowEarnersDetailsList                | Body                               | Yes                                     | `JS Array`                                                                                           | `*See example*`                        |

**`lowEarnersDetailsList` array item definition:**

| <div style="width:200px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:200px">Format</div>                                                                | <div style="width:175px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|------------------------------------------------------------------------------------------------------|----------------------------------------|
| taxYear                              | Body                               | Yes                                     | `Integer`<br/> Year of tax period start                                                              | `2025`                                 |
| lowEarnersCalculations               | Body                               | Yes                                     | `JS Array`                                                                                           | `*See example*`                        |

**`lowEarnersCalculations` array item definition:**

| <div style="width:200px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:200px">Format</div>                                                                | <div style="width:175px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|------------------------------------------------------------------------------------------------------|----------------------------------------|
| lowEarnersClaimDetails               | Body                               | Yes                                     | `JS Object`                                                                                          | `*See example*`                        |
| lowEarnersDataDetails                | Body                               | Yes                                     | `JS Object`                                                                                          | `*See example*`                        |

**`lowEarnersClaimDetails` object definition:**

| <div style="width:200px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:200px">Format</div>                                                     | <div style="width:175px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|-------------------------------------------------------------------------------------------|----------------------------------------|
| claimSequenceNumber                  | Body                               | Yes                                     | `0 <= Integer <= 32766`                                                                   | `132`                                  |
| entitlementAmount                    | Body                               | No                                      | `Positive decimal`[(**See here**)](#common-formats)                                       | `230.55`                               |
| claimStatus                          | Body                               | Yes                                     | One of:<br/> -`"CANCELLED"` <br/> -`"PAID"` <br/> -`"PENDING"` <br/> -`"SUSPENDED - RLS"` | `132`                                  |
| inSelfAssessment                     | Body                               | Yes                                     | `Boolean`                                                                                 | `false`                                |
| calculationDate                      | Body                               | No                                      | `Local date string`[(**See here**)](#common-formats)                                      | `"2022-06-27"`                         |
| claimDate                            | Body                               | No                                      | `Local date string`[(**See here**)](#common-formats)                                      | `"2022-06-27"`                         |
| reminderOutputSent                   | Body                               | Yes                                     | `Boolean`                                                                                 | `132`                                  |
| reissueClaimOutput                   | Body                               | Yes                                     | `Boolean`                                                                                 | `132`                                  |
| originalAmount                       | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats)                                       | `132`                                  |

**`lowEarnersDataDetails` object definition:**

| <div style="width:200px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:200px">Format</div>               | <div style="width:175px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|-----------------------------------------------------|----------------------------------------|
| responseTimestamp                    | Body                               | No                                      | `Datetime string`[(**See here**)](#common-formats)  | `"2022-06-27 09:12:28"`                |
| calculationSequenceNumber            | Body                               | Yes                                     | `0 <= Integer <= 65534`                             | `132`                                  |
| dataSourceMaster                     | Body                               | Yes                                     | One of: <br/>-`"CESA"` <br/>-`"ITSA"` <br/>-`"NPS"` | `"NPS"`                                |
| netPayContributionsTotal             | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats) | `1234.56`                              |
| basicRatePercentage                  | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats) | `1234.56`                              |
| totalAllowances                      | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats) | `1234.56`                              |
| totalIncome                          | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats) | `1234.56`                              |
| totalDeductions                      | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats) | `1234.56`                              |
| reissueClaimOutput                   | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats) | `1234.56`                              |

<br/>

#### 200 OK - Example response body:
Eligible user:
<a id=eligible-user></a>
```json 
{
  "status": "PAYMENTS_AVAILABLE",
  "data": {
     "currentLowEarnersOptimisticLock": 86,
     "identifier": "AA000001A",
     "lowEarnersDetailsList": [
        {
           "taxYear": 2022,
           "lowEarnersCalculations": [
              {
                 "lowEarnersClaimDetails": {
                    "claimSequenceNumber": 123,
                    "calculationDate": "2022-06-27",
                    "claimDate": "2022-06-27",
                    "claimStatus": "PAID",
                    "entitlementAmount": 10.56,
                    "inSelfAssessment": true,
                    "originalAmount": 10.56,
                    "reissueClaimOutput": true,
                    "reminderOutputSent": true
                 },
                 "lowEarnersDataDetails": {
                    "calculationSequenceNumber": 123,
                    "basicRatePercentage": 10.56,
                    "dataSourceMaster": "CESA",
                    "netPayContributionsTotal": 10.56,
                    "responseTimestamp": "2022-06-27 09:12:28",
                    "totalAllowances": 10.56,
                    "totalDeductions": 10.56,
                    "totalIncome": 10.56,
                    "totalTaxDue": 10.56
                 }
              },
              {
                 "lowEarnersClaimDetails": {
                    "claimSequenceNumber": 123,
                    "calculationDate": "2022-06-27",
                    "claimDate": "2022-06-27",
                    "claimStatus": "PENDING",
                    "entitlementAmount": 10.56,
                    "inSelfAssessment": true,
                    "originalAmount": 10.56,
                    "reissueClaimOutput": true,
                    "reminderOutputSent": true
                 },
                 "lowEarnersDataDetails": {
                    "calculationSequenceNumber": 123,
                    "basicRatePercentage": 10.56,
                    "dataSourceMaster": "CESA",
                    "netPayContributionsTotal": 10.56,
                    "responseTimestamp": "2022-06-27 09:12:28",
                    "totalAllowances": 10.56,
                    "totalDeductions": 10.56,
                    "totalIncome": 10.56,
                    "totalTaxDue": 10.56
                 }
              }
           ]
        },
        {
           "taxYear": 2023,
           "lowEarnersCalculations": [
              {
                 "lowEarnersClaimDetails": {
                    "claimSequenceNumber": 123,
                    "calculationDate": "2023-06-27",
                    "claimDate": "2023-06-27",
                    "claimStatus": "CANCELLED",
                    "entitlementAmount": 10.56,
                    "inSelfAssessment": true,
                    "originalAmount": 10.56,
                    "reissueClaimOutput": true,
                    "reminderOutputSent": true
                 },
                 "lowEarnersDataDetails": {
                    "calculationSequenceNumber": 123,
                    "basicRatePercentage": 10.56,
                    "dataSourceMaster": "CESA",
                    "netPayContributionsTotal": 10.56,
                    "responseTimestamp": "2023-06-27 09:12:28",
                    "totalAllowances": 10.56,
                    "totalDeductions": 10.56,
                    "totalIncome": 10.56,
                    "totalTaxDue": 10.56
                 }
              }
           ]
        }
     ]
  }
}
```

**[See all test cases](#available-test-scenarios)**

<br/>

## Retrieve LEPP Details

---

### Request details

#### Request Url:

~~~
GET /low-earners-pensions-payment/get-payment-details
~~~

#### Request headers:

| <div style="width:120px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:150px">Format</div> | <div style="width:250px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|---------------------------------------|----------------------------------------|
| correlationId                        | Header                             | Yes                                     | [**See here**](#common-formats)       | `e470d658-99f7-4292-a4a1-ed12c72f1337` |
| authorization                        | Header                             | Yes                                     | OAuth 2.0 Bearer token                | `Bearer XXXXXXXXXXXXX`                 |


#### Request body:

`No request body`

<br/>

### Response details

#### Response headers:

| <div style="width:120px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:150px">Format</div> | <div style="width:250px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|---------------------------------------|----------------------------------------|
| correlationId                        | Header                             | Yes                                     | [**See here**](#common-formats)       | `e470d658-99f7-4292-a4a1-ed12c72f1337` |

#### Error responses:
This API may return the following error statuses:
- [400 - Bad Request](#service-errors)
- [404 - Not Found](#service-errors)
- [500 - Internal Server Error](#service-errors)

#### 200 OK - Response body:

`application/json;charset=UTF-8`

| <div style="width:200px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:200px">Format</div>                                                                | <div style="width:175px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|------------------------------------------------------------------------------------------------------|----------------------------------------|
| currentLowEarnersOptimisticLock      | Body                               | Yes                                     | `0 <= Integer <= 254`                                                                                | `132`                                  |
| identifier                           | Body                               | Yes                                     | `NINO` [**(See here)**](https://www.gov.uk/hmrc-internal-manuals/national-insurance-manual/nim39110) | `QQ123456A`                            |
| lowEarnersDetailsList                | Body                               | Yes                                     | `JS Array`                                                                                           | `*See example*`                        |

**`lowEarnersDetailsList` array item definition:**

| <div style="width:200px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:200px">Format</div>                                                                | <div style="width:175px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|------------------------------------------------------------------------------------------------------|----------------------------------------|
| taxYear                              | Body                               | Yes                                     | `Integer`<br/> Year of tax period start                                                              | `2025`                                 |
| lowEarnersCalculations               | Body                               | Yes                                     | `JS Array`                                                                                           | `*See example*`                        |

**`lowEarnersCalculations` array item definition:**

| <div style="width:200px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:200px">Format</div>                                                                | <div style="width:175px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|------------------------------------------------------------------------------------------------------|----------------------------------------|
| lowEarnersClaimDetails               | Body                               | Yes                                     | `JS Object`                                                                                          | `*See example*`                        |
| lowEarnersDataDetails                | Body                               | Yes                                     | `JS Object`                                                                                          | `*See example*`                        |

**`lowEarnersClaimDetails` object definition:**

| <div style="width:200px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:200px">Format</div>                                                     | <div style="width:175px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|-------------------------------------------------------------------------------------------|----------------------------------------|
| claimSequenceNumber                  | Body                               | Yes                                     | `0 <= Integer <= 32766`                                                                   | `132`                                  |
| entitlementAmount                    | Body                               | No                                      | `Positive decimal`[(**See here**)](#common-formats)                                       | `230.55`                               |
| claimStatus                          | Body                               | Yes                                     | One of:<br/> -`"CANCELLED"` <br/> -`"PAID"` <br/> -`"PENDING"` <br/> -`"SUSPENDED - RLS"` | `132`                                  |
| inSelfAssessment                     | Body                               | Yes                                     | `Boolean`                                                                                 | `false`                                |
| calculationDate                      | Body                               | No                                      | `Local date string`[(**See here**)](#common-formats)                                      | `"2022-06-27"`                         |
| claimDate                            | Body                               | No                                      | `Local date string`[(**See here**)](#common-formats)                                      | `"2022-06-27"`                         |
| reminderOutputSent                   | Body                               | Yes                                     | `Boolean`                                                                                 | `132`                                  |
| reissueClaimOutput                   | Body                               | Yes                                     | `Boolean`                                                                                 | `132`                                  |
| originalAmount                       | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats)                                       | `132`                                  |

**`lowEarnersDataDetails` object definition:**

| <div style="width:200px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:200px">Format</div>               | <div style="width:175px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|-----------------------------------------------------|----------------------------------------|
| responseTimestamp                    | Body                               | No                                      | `Datetime string`[(**See here**)](#common-formats)  | `"2022-06-27 09:12:28"`                |
| calculationSequenceNumber            | Body                               | Yes                                     | `0 <= Integer <= 65534`                             | `132`                                  |
| dataSourceMaster                     | Body                               | Yes                                     | One of: <br/>-`"CESA"` <br/>-`"ITSA"` <br/>-`"NPS"` | `"NPS"`                                |
| netPayContributionsTotal             | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats) | `1234.56`                              |
| basicRatePercentage                  | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats) | `1234.56`                              |
| totalAllowances                      | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats) | `1234.56`                              |
| totalIncome                          | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats) | `1234.56`                              |
| totalDeductions                      | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats) | `1234.56`                              |
| reissueClaimOutput                   | Body                               | No                                      | `Positive decimal`[**(See here)**](#common-formats) | `1234.56`                              |

<br/>

#### 200 OK - Example response body:
Eligible user:
<a id=eligible-user></a>
```json 
{
  "currentLowEarnersOptimisticLock": 86,
  "identifier": "AA000001A",
  "lowEarnersDetailsList": [
    {
      "taxYear": 2022,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2022-06-27",
            "claimDate": "2022-06-27",
            "claimStatus": "PAID",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
            "originalAmount": 10.56,
            "reissueClaimOutput": true,
            "reminderOutputSent": true
          },
          "lowEarnersDataDetails": {
            "calculationSequenceNumber": 123,
            "basicRatePercentage": 10.56,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "responseTimestamp": "2022-06-27 09:12:28",
            "totalAllowances": 10.56,
            "totalDeductions": 10.56,
            "totalIncome": 10.56,
            "totalTaxDue": 10.56
          }
        },
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2022-06-27",
            "claimDate": "2022-06-27",
            "claimStatus": "PENDING",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
            "originalAmount": 10.56,
            "reissueClaimOutput": true,
            "reminderOutputSent": true
          },
          "lowEarnersDataDetails": {
            "calculationSequenceNumber": 123,
            "basicRatePercentage": 10.56,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "responseTimestamp": "2022-06-27 09:12:28",
            "totalAllowances": 10.56,
            "totalDeductions": 10.56,
            "totalIncome": 10.56,
            "totalTaxDue": 10.56
          }
        }
      ]
    },
    {
      "taxYear": 2023,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2023-06-27",
            "claimDate": "2023-06-27",
            "claimStatus": "CANCELLED",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
            "originalAmount": 10.56,
            "reissueClaimOutput": true,
            "reminderOutputSent": true
          },
          "lowEarnersDataDetails": {
            "calculationSequenceNumber": 123,
            "basicRatePercentage": 10.56,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "responseTimestamp": "2023-06-27 09:12:28",
            "totalAllowances": 10.56,
            "totalDeductions": 10.56,
            "totalIncome": 10.56,
            "totalTaxDue": 10.56
          }
        }
      ]
    }
  ]
}
```

**[See all test cases](#available-test-scenarios)**

<br/>

## Common formats

---

### Fields
| <div style="width:120px">Format name</div> | <div style="width:250px">Format</div>                                   | <div style="width:295px">Field</div>     |
|--------------------------------------------|-------------------------------------------------------------------------|------------------------------------------|
| Correlation Id                             | `^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}`<br/>`-[0-9a-f]{4}-[0-9a-f]{12}$` | `"e470d658-99f7-4292-a4a1-ed12c72f1337"` |
| Positive decimal                           | `0 <= decimal <= 99999999999999.98`<br/>`2 decimal places`              | `123456.78`                              |
| Local date string                          | `YYYY-MM-DD`                                                            | `"2025-12-28"`                           |
| Datetime string                            | `YYYY-MM-DD HH:MM:SS`                                                   | `"2025-12-28" 09:13:23`                  |

<br/>

### Service errors

#### Error response headers:

| <div style="width:120px">Field</div> | <div style="width:50px">Type</div> | <div style="width: 60px">Required</div> | <div style="width:150px">Format</div> | <div style="width:270px">Example</div> |
|--------------------------------------|------------------------------------|-----------------------------------------|---------------------------------------|----------------------------------------|
| correlationId                        | Header                             | Yes                                     | [**See here**](#common-formats)   | `e470d658-99f7-4292-a4a1-ed12c72f1337` |


#### Error response body:
`application/json;charset=UTF-8`

| <div style="width:100px">Field</div> | <div style="width:50px">Type</div> | <div style="width:60px">Required</div> | <div style="width:185px">Format</div> | <div style="width:150px">Example</div> |
|--------------------------------------|------------------------------------|----------------------------------------|---------------------------------------|----------------------------------------|
| code                                 | Body                               | Yes                                    | `String`                              | `SOME_CODE`                            |
| message                              | Body                               | Yes                                    | `String`                              | `Some message`                         |

#### Example response body:
```json
{
  "code": "SOME_CODE",
  "message": "Some message"
}
```

<br/>

## Available test scenarios

---

### How to use the scenarios

The test scenarios are achieved through using certain NINO prefixes when creating a test account. For example, any NINO
which starts with the characters `AA1` will achieve the default success outcome for the `Retrieve LEPP Details`, 
`Retrieve LEPP Summary`, and `Accept LEPP Payment` endpoints. The full list of NINO prefixes, and the corresponding test
scenario each will trigger is listed below.

The `Accept LEPP Payment` endpoint appears later in the LEPP journey than the `Retrieve LEPP Details` endpoint. To allow
the error scenarios on the later endpoint to be testable the stub has been set up so that any NINO prefix relating to
those scenarios will result in a default success when provided to `Retrieve LEPP Details`. This is to allow requests to
'pass-through' the first API call so that the second may fail. For example, using an account with a NINO beginning with
`ER2503` will result in a default success from the `Retrieve LEPP Details` endpoint and a 503 error response from
`Accept LEPP Payment`.

### Scenarios

#### [Retrieve LEPP Summary](#retrieve-lepp-summary)

| <div style="width:200px">Scenario</div> | <div style="width:300px">Description</div>                                                                                                              | <div style="width:75px">Identifier</div>    | <div style="width:50px">Status</div> | <div style="width:100px">Content</div>                        |
|-----------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------|--------------------------------------|---------------------------------------------------------------|
| Default success                         | A success response with `200` status code where<br/>the user has a mixture of all four supported claim<br/>status values across 4 tax years (2022-2025) | `AA1******`                                 | `200`                                | [Default success](#match-result)                              |
| None available success                  | A success response with `200` status code where<br/>the user has no available payments                                                                  | `AA2******`                                 | `200`                                | [None available](#match-result)                               |
| Not eligible success                    | A success response with `200` status code where<br/>the user has no applicable data                                                                     | `AA3******`<br/>`AA4******`<br/>`AA5******` | `200`                                | [Not eligible](./resources/EXAMPLES.MD#not-eligible-scenario) |
| Bad request error                       | An error response with `400` status code                                                                                                                | `ER14001**`<br/>`ER14002**`                 | `400`                                | [Service error](#service-errors)                              |
| Internal server error                   | An error response with `500` status code                                                                                                                | `ER1500***`<br/>`ER1503***`                 | `500`                                | [Service error](#service-errors)                              |

<br/>

#### [Retrieve LEPP Details](#retrieve-lepp-details)

| <div style="width:200px">Scenario</div> | <div style="width:300px">Description</div>                                                                                                            | <div style="width:75px">Identifier</div> | <div style="width:50px">Status</div> | <div style="width:100px">Content</div>                                                           |
|-----------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------|--------------------------------------|--------------------------------------------------------------------------------------------------|
| Default success                         | Success response with `200` status code where<br/>the user has a mixture of all four supported claim<br/>status values across 4 tax years (2022-2025) | `AA1******`                              | `200`                                | [Default success](./resources/EXAMPLES.MD#default-success-details-scenario)                      |
| 'None available' success                | Success response with `200` status code where<br/>the user is eligible with no available payments                                                     | `AA2******`                              | `200`                                | [None available](./resources/EXAMPLES.MD#none-available-details-scenario)                        |
| 'Capacitor' success                     | Success response with `200` status code where<br/>the user has 'available capcitor' payment data                                                      | `AA3******`                              | `200`                                | [Capacitor](./resources/EXAMPLES.MD#capacitor-details-scenario)                                  |
| 'Deceased capacitor' success            | Success response with `200` status code where<br/>the user has 'deceased capcitor' payment data                                                       | `AA4******`                              | `200`                                | [Deceased with<br/>capacitor](./resources/EXAMPLES.MD#deceased-capacitor-details-scenario)       |
| 'Deceased no capacitor' success         | Success response with `200` status code where<br/>the user has 'deceased no capcitor' payment data                                                    | `AA5******`                              | `200`                                | [Deceased with<br/>no capacitor](./resources/EXAMPLES.MD#deceased-no-capacitor-details-scenario) |
| Bad request error                       | Error response with `400` status code                                                                                                                 | `ER14001**`<br/>`ER14002**`              | `400`                                | [Service error](#service-errors)                                                                 |
| Forbidden error                         | Error response with `403` status code                                                                                                                 | `ER1403***`                              | `403`                                | [Service error](#service-errors)                                                                 |
| Not Found error                         | Error response with `404` status code                                                                                                                 | `ER1404***`                              | `404`                                | [Service error](#service-errors)                                                                 |
| Internal server error                   | Error response with `500` status code                                                                                                                 | `ER1500***`                              | `500`                                | [Service error](#service-errors)                                                                 |
| Service unavailable error               | Error response with `503` status code                                                                                                                 | `ER1503***`                              | `503`                                | [Service error](#service-errors)                                                                 |

<br/>

#### [Accept LEPP Payment](#)

| <div style="width:200px">Scenario</div> | <div style="width:300px">Description</div> | <div style="width:75px">Identifier</div> | <div style="width:50px">Status</div> | <div style="width:100px">Content</div> |
|-----------------------------------------|--------------------------------------------|------------------------------------------|--------------------------------------|----------------------------------------|
| Default success                         | Success response with `201` status code    | `AA*******`                              | `201`                                | [Default success](#match-result)       |
| Bad request error                       | Error response with `400` status code      | `ER24001**`<br/>`ER24002**`              | `400`                                | [Service error](#service-errors)       |
| Forbidden error                         | Error response with `403` status code      | `ER2403***`                              | `403`                                | [Service error](#service-errors)       |
| Not Found error                         | Error response with `404` status code      | `ER2404***`                              | `404`                                | [Service error](#service-errors)       |
| Conflict error                          | Error response with `409` status code      | `ER2409***`                              | `409`                                | [Service error](#service-errors)       |
| Unprocessable Entity error              | Error response with `422` status code      | `ER2422***`                              | `422`                                | [Service error](#service-errors)       |
| Internal server error                   | Error response with `500` status code      | `ER2500***`                              | `500`                                | [Service error](#service-errors)       |
| Service unavailable error               | Error response with `503` status code      | `ER2503***`                              | `503`                                | [Service error](#service-errors)       |

<br/>


## License

---

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

<br/>
