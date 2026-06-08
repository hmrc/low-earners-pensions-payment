# Stub errors

## What is this page?

---

This page details the error responses returned directly from the lepp-nps-stub microservice. For error responses returned by the low-earners-pensions-payment microservice see [available test scenarios](../README.MD#available-test-scenarios).

## Error scenarios

---

### How to use the scenarios
Each test case is achievable through use of a valid NINO which has a matching prefix to a pre-existing scenario. For example by using a NINO starting with `AA1` the default success scenarios will be returned for both endpoints. Any NINOs associated with an `Accept LEPP Payment` test case will trigger a success scenario when used for the `Retrieve LEPP Details` endpoint. This is to allow errors to 'pass-through' calls to the `retrieve` endpoint, which is always called first in a normal LEPP user journey.

### Available scenarios

#### [Retrieve LEPP Details](../README.MD#retrieve-lepp-details)
| NINO prefix | Description                  | <div style="width:50px">Status</div> | <div style="width:140px">Stub content</div> | <div style="width:100px">Backend result</div> |
|-------------|------------------------------|--------------------------------------|---------------------------------------------|-----------------------------------------------|
| `ER14001**` | Bad request error (format 1) | `400`                                | [Bad request 1](#bad-request-1)             | [Default success](#match-result)              |
| `ER14002**` | Bad request error (format 2) | `400`                                | [Bad request 2](#match-result)              | [Default success](#match-result)              |
| `ER1403***` | Forbidden error              | `403`                                | [Forbidden](#forbidden)                     | [Default success](#match-result)              |
| `ER1404***` | Not found error              | `404`                                | [Not found with body](#not-found-body)      | [Default success](#match-result)              |
| `ER1500***` | Internal server error        | `500`                                | [Internal server error](#server-error)      | [Default success](#match-result)              |
| `ER1503***` | Service unavailable error    | `503`                                | [Service unavailable](#server-error)        | [Default success](#match-result)              |

<br/>

#### [Accept LEPP Payment](../README.MD#accept-lepp-payment)
| NINO prefix | Description                  | <div style="width:50px">Status</div> | <div style="width:140px">Stub content</div>   | <div style="width:100px">Backend result</div> |
|-------------|------------------------------|--------------------------------------|-----------------------------------------------|-----------------------------------------------|
| `ER24001**` | Bad request error (format 1) | `400`                                | [Default success](#bad-request-1)             | [Default success](#match-result)              |
| `ER24002**` | Bad request error (format 2) | `400`                                | [Default success](#bad-request-2)             | [Default success](#match-result)              |
| `ER2403***` | Forbidden error              | `403`                                | [Forbidden success](#forbidden)               | [Default success](#match-result)              |
| `ER2404***` | Not found error              | `404`                                | [Not found without body](#not-found-no-body)  | [Default success](#match-result)              |
| `ER2409***` | Conflict error               | `409`                                | [Conflict](#conflict)                         | [Default success](#match-result)              |
| `ER2422***` | Unprocessable entity error   | `422`                                | [Unprocessable entity](#unprocessable-entity) | [Default success](#match-result)              |
| `ER2500***` | Internal server error        | `500`                                | [Internal server error](#server-error)        | [Default success](#match-result)              |
| `ER2503***` | Service unavailable error    | `503`                                | [Service unavailable](#server-error)          | [Default success](#match-result)              |

<br/>

## Error formatting

---

### Error response headers:

| <div style="width:100px">Field</div> | <div style="width:50px">Type</div> | <div style="width:60px">Required</div> | <div style="width:185px">Format</div>                                          | <div style="width:270px">Example</div> |
|--------------------------------------|------------------------------------|----------------------------------------|--------------------------------------------------------------------------------|----------------------------------------|
| correlationId                        | Header                             | Yes                                    | `^[0-9a-f]{8}-[0-9a-f]{4}`<br/>`-[0-9a-f]{4}-[0-9a-f]{4}`<br/>`-[0-9a-f]{12}$` | `e470d658-99f7-4292-a4a1-ed12c72f1337` |

<br/>

### 400 - Bad Request

#### Response body:
`application/json;charset=UTF-8`

~~~
Returned by:
- Retrieve LEPP Details
- Accept LEPP Payment
~~~

**N.B** - Returned error format will either include the fields `type` and `reason` OR the fields `reason` and `code`

| <div style="width:100px">Field</div> | <div style="width:50px">Type</div> | <div style="width:60px">Required</div> | <div style="width:185px">Format</div> | <div style="width:270px">Example</div> |
|--------------------------------------|------------------------------------|----------------------------------------|---------------------------------------|----------------------------------------|
| origin                               | Body                               | Yes                                    | `"HIP"` or `"HOD"`                    | `"HIP"`                                |
| response                             | Body                               | Yes                                    | `JS Object`                           | `*See example body*`                   |
| failures                             | Body                               | Yes                                    | `Non-empty JS Array`                  | `*See example body*`                   |
| type OR reason                       | Body                               | Yes                                    | `String`                              | `Failure type`                         |
| reason OR code                       | Body                               | Yes                                    | `String`                              | `Failure reason`                       |

#### Example response body:
Format 1:
<a id="bad-request-1"></a>
```json
{
  "origin": "HIP",
  "response": {
    "failures": [
      {
        "type": "Error type",
        "reason": "Error reason"
      }
    ]
  }
}
```

Format 2:
<a id="bad-request-2"></a>
```json
{
  "origin": "HIP",
  "response": {
    "failures": [
      {
        "reason": "Error reason",
        "code": "Error code"
      }
    ]
  }
}
```

<br/>

### 403 - Forbidden
<a id="forbidden"></a>

#### Response body:
`application/json;charset=UTF-8`

~~~
Returned by:
- Retrieve LEPP Details
- Accept LEPP Payment
~~~

| <div style="width:100px">Field</div> | <div style="width:50px">Type</div> | <div style="width:60px">Required</div> | <div style="width:185px">Format</div> | <div style="width:270px">Example</div> |
|--------------------------------------|------------------------------------|----------------------------------------|---------------------------------------|----------------------------------------|
| reason                               | Body                               | Yes                                    | `String`                              | `Failure reason`                       |
| code                                 | Body                               | Yes                                    | `String`                              | `Failure code`                         |

#### Example response body:
```json
{
  "reason": "Error reason",
  "code": "Error code"
}
```

<br/>

### 404 - Not Found
<a id="not-found-with-no-body"></a>

#### Response body:

~~~
Returned by:
- Accept LEPP Payment
~~~

`No response body`

<a id="not-found-with-body"></a>
#### Response body:

~~~
Returned by:
- Retrieve LEPP Details
~~~

| <div style="width:100px">Field</div> | <div style="width:50px">Type</div> | <div style="width:60px">Required</div> | <div style="width:185px">Format</div> | <div style="width:270px">Example</div> |
|--------------------------------------|------------------------------------|----------------------------------------|---------------------------------------|----------------------------------------|
| reason                               | Body                               | Yes                                    | `String`                              | `Failure reason`                       |
| code                                 | Body                               | Yes                                    | `String`                              | `Failure code`                         |

#### Example response body:
```json
{
  "reason": "Error reason",
  "code": "Error code"
}
```

<br/>

### 409 - Conflict
<a id="conflict"></a>

#### Response body:
`application/json;charset=UTF-8`

~~~
Returned by:
- Accept LEPP Payment
~~~

| <div style="width:100px">Field</div> | <div style="width:50px">Type</div> | <div style="width:60px">Required</div> | <div style="width:185px">Format</div> | <div style="width:270px">Example</div> |
|--------------------------------------|------------------------------------|----------------------------------------|---------------------------------------|----------------------------------------|
| reason                               | Body                               | Yes                                    | `String`                              | `Failure reason`                       |
| code                                 | Body                               | Yes                                    | `String`                              | `Failure code`                         |

#### Example response body:
```json
{
  "reason": "Error reason",
  "code": "Error code"
}
```

<br/>

### 422 - Unprocessable Entity
<a id="unprocessable-entity"></a>

#### Response body:
`application/json;charset=UTF-8`

~~~
Returned by:
- Accept LEPP Payment
~~~

| <div style="width:100px">Field</div> | <div style="width:50px">Type</div> | <div style="width:60px">Required</div> | <div style="width:185px">Format</div> | <div style="width:270px">Example</div> |
|--------------------------------------|------------------------------------|----------------------------------------|---------------------------------------|----------------------------------------|
| failures                             | Body                               | Yes                                    | `Non-empty JS Array`                  | `*See example body*`                   |
| reason                               | Body                               | Yes                                    | `String`                              | `Failure reason`                       |
| code                                 | Body                               | Yes                                    | `String`                              | `Failure code`                         |

#### Example response body:
```json
{
  "failures": [
    {
      "reason": "Error reason",
      "code": "Error code"
    }
  ]
}
```

<br/>

### 500 - Internal Server Error / 503 - Service Unavailable
<a id="server-error"></a>

#### Response body:
`application/json;charset=UTF-8`

~~~
Returned by:
- Retrieve LEPP Summary
- Retrieve LEPP Details
~~~

| <div style="width:100px">Field</div> | <div style="width:50px">Type</div> | <div style="width:60px">Required</div> | <div style="width:185px">Format</div> | <div style="width:270px">Example</div> |
|--------------------------------------|------------------------------------|----------------------------------------|---------------------------------------|----------------------------------------|
| origin                               | Body                               | Yes                                    | `"HIP"` or `"HOD"`                    | `"HIP"`                                |
| response                             | Body                               | Yes                                    | `JS Object`                           | `See example body`                     |
| failures                             | Body                               | Yes                                    | `Non-empty JS Array`                  | `See example body`                     |
| type                                 | Body                               | Yes                                    | `String`                              | `Failure type`                         |
| reason                               | Body                               | Yes                                    | `String`                              | `Failure reason`                       |

#### Example response body:
```json
{
  "origin": "HIP",
  "response": {
    "failures": [
      {
        "type": "Error type",
        "reason": "Error reason"
      }
    ]
  }
}
```

<br/>

## License

---

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

<br/>