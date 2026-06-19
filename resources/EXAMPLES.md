# mpe-nps stub

## Retrieve LEPP Summary

---

### Default success summary scenario:
```json
{
  "status": "PAYMENTS_AVAILABLE",
  "data": {
    "currentLowEarnersOptimisticLock": 86,
    "identifier": "STUB_NOT_USED",
    "lowEarnersDetailsList": [
      {
        "taxYear": 2024,
        "lowEarnersCalculations": [
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "PAID",
              "inSelfAssessment": true,
              "calculationDate": "2024-06-27",
              "claimDate": "2024-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2024-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
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
              "entitlementAmount": 10.56,
              "claimStatus": "SUSPENDED - RLS",
              "inSelfAssessment": true,
              "calculationDate": "2023-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2023-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          }
        ]
      },
      {
        "taxYear": 2025,
        "lowEarnersCalculations": [
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "CANCELLED",
              "inSelfAssessment": true,
              "calculationDate": "2025-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2025-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          }
        ]
      },
      {
        "taxYear": 2022,
        "lowEarnersCalculations": [
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "PENDING",
              "inSelfAssessment": true,
              "calculationDate": "2022-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2022-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          }
        ]
      }
    ]
  }
}
```

### 'None available' summary scenario:
```json
{
  "status": "NO_ACTIONS",
  "data": {
    "currentLowEarnersOptimisticLock": 88,
    "identifier": "STUB_NOT_USED",
    "lowEarnersDetailsList": [
      {
        "taxYear": 2024,
        "lowEarnersCalculations": [
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "PAID",
              "inSelfAssessment": true,
              "calculationDate": "2024-06-27",
              "claimDate": "2024-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2024-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          },
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "PAID",
              "inSelfAssessment": true,
              "calculationDate": "2024-06-27",
              "claimDate": "2024-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true,
              "originalAmount": 10.56
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2024-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
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
              "entitlementAmount": 10.56,
              "claimStatus": "CANCELLED",
              "inSelfAssessment": true,
              "calculationDate": "2023-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2023-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          }
        ]
      },
      {
        "taxYear": 2022,
        "lowEarnersCalculations": [
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "PAID",
              "inSelfAssessment": true,
              "calculationDate": "2022-06-27",
              "claimDate": "2022-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2022-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          }
        ]
      }
    ]
  }
}
```

### 'Not eligible' summary scenario:
```json
{
  "status": "NOT_ELIGIBLE"
}
```

### 'Suspended' summary scenario:
```json
{
  "status": "CHECK",
  "data": {
    "currentLowEarnersOptimisticLock": 93,
    "identifier": "STUB_NOT_USED",
    "lowEarnersDetailsList": [
      {
        "taxYear": 2024,
        "lowEarnersCalculations": [
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "SUSPENDED - RLS",
              "inSelfAssessment": true,
              "calculationDate": "2024-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2024-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
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
              "entitlementAmount": 10.56,
              "claimStatus": "SUSPENDED - RLS",
              "inSelfAssessment": true,
              "calculationDate": "2023-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2023-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          }
        ]
      },
      {
        "taxYear": 2025,
        "lowEarnersCalculations": [
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "SUSPENDED - RLS",
              "inSelfAssessment": true,
              "calculationDate": "2025-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2025-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          }
        ]
      },
      {
        "taxYear": 2022,
        "lowEarnersCalculations": [
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "SUSPENDED - RLS",
              "inSelfAssessment": true,
              "calculationDate": "2022-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2022-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          }
        ]
      }
    ]
  }
}
```

### 'Underpayment' summary scenario:
```json
{
  "status": "PAYMENTS_AVAILABLE",
  "data": {
    "currentLowEarnersOptimisticLock": 94,
    "identifier": "STUB_NOT_USED",
    "lowEarnersDetailsList": [
      {
        "taxYear": 2024,
        "lowEarnersCalculations": [
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "PENDING",
              "inSelfAssessment": true,
              "calculationDate": "2024-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2024-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
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
              "entitlementAmount": 10.56,
              "claimStatus": "PAID",
              "inSelfAssessment": true,
              "calculationDate": "2023-06-27",
              "claimDate": "2023-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2023-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          },
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "PAID",
              "inSelfAssessment": true,
              "calculationDate": "2023-06-27",
              "claimDate": "2023-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true,
              "originalAmount": 10.56
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2023-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          }
        ]
      },
      {
        "taxYear": 2022,
        "lowEarnersCalculations": [
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "PENDING",
              "inSelfAssessment": true,
              "calculationDate": "2022-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true,
              "originalAmount": 10.56
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2022-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          },
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "PAID",
              "inSelfAssessment": true,
              "calculationDate": "2022-06-27",
              "claimDate": "2022-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2022-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          }
        ]
      }
    ]
  }
}
```

<br/>

### 'Multiple payments' summary scenario:
```json
{
  "status": "PAYMENTS_AVAILABLE",
  "data": {
    "currentLowEarnersOptimisticLock": 95,
    "identifier": "STUB_NOT_USED",
    "lowEarnersDetailsList": [
      {
        "taxYear": 2024,
        "lowEarnersCalculations": [
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "PENDING",
              "inSelfAssessment": true,
              "calculationDate": "2024-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2024-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
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
              "entitlementAmount": 10.56,
              "claimStatus": "PENDING",
              "inSelfAssessment": true,
              "calculationDate": "2023-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2023-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          }
        ]
      },
      {
        "taxYear": 2025,
        "lowEarnersCalculations": [
          {
            "lowEarnersClaimDetails": {
              "claimSequenceNumber": 123,
              "entitlementAmount": 10.56,
              "claimStatus": "PENDING",
              "inSelfAssessment": true,
              "calculationDate": "2025-06-27",
              "reminderOutputSent": true,
              "reissueClaimOutput": true
            },
            "lowEarnersDataDetails": {
              "responseTimestamp": "2025-06-27 09:12:28",
              "calculationSequenceNumber": 123,
              "dataSourceMaster": "CESA",
              "netPayContributionsTotal": 10.56,
              "basicRatePercentage": 10.56,
              "totalAllowances": 10.56,
              "totalIncome": 10.56,
              "totalDeductions": 10.56,
              "totalTaxDue": 10.56
            }
          }
        ]
      }
    ]
  }
}
```

<br/>


## Retrieve LEPP Details

---

### Default success details scenario:
```json
{
  "currentLowEarnersOptimisticLock": 86,
  "identifier": "STUB_NOT_USED",
  "lowEarnersDetailsList": [
    {
      "taxYear": 2024,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2024-06-27",
            "claimDate": "2024-06-27",
            "claimStatus": "PAID",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
            "reissueClaimOutput": true,
            "reminderOutputSent": true
          },
          "lowEarnersDataDetails": {
            "calculationSequenceNumber": 123,
            "basicRatePercentage": 10.56,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "responseTimestamp": "2024-06-27 09:12:28",
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
            "claimStatus": "SUSPENDED - RLS",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
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
    },
    {
      "taxYear": 2025,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2025-06-27",
            "claimStatus": "CANCELLED",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
            "reissueClaimOutput": true,
            "reminderOutputSent": true
          },
          "lowEarnersDataDetails": {
            "calculationSequenceNumber": 123,
            "basicRatePercentage": 10.56,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "responseTimestamp": "2025-06-27 09:12:28",
            "totalAllowances": 10.56,
            "totalDeductions": 10.56,
            "totalIncome": 10.56,
            "totalTaxDue": 10.56
          }
        }
      ]
    },
    {
      "taxYear": 2022,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2022-06-27",
            "claimStatus": "PENDING",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
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
    }
  ]
}
```

### 'None available' details scenario:
```json
{
  "currentLowEarnersOptimisticLock": 88,
  "identifier": "STUB_NOT_USED",
  "lowEarnersDetailsList": [
    {
      "taxYear": 2024,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2024-06-27",
            "claimDate": "2024-06-27",
            "claimStatus": "PAID",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
            "reissueClaimOutput": true,
            "reminderOutputSent": true
          },
          "lowEarnersDataDetails": {
            "calculationSequenceNumber": 123,
            "basicRatePercentage": 10.56,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "responseTimestamp": "2024-06-27 09:12:28",
            "totalAllowances": 10.56,
            "totalDeductions": 10.56,
            "totalIncome": 10.56,
            "totalTaxDue": 10.56
          }
        },
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2024-06-27",
            "claimDate": "2024-06-27",
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
            "responseTimestamp": "2024-06-27 09:12:28",
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
            "claimStatus": "CANCELLED",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
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
    },
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
    }
  ]
}
```

### 'Capacitor' details scenario:
```json
{
  "currentLowEarnersOptimisticLock": 89,
  "identifier": "STUB_NOT_USED",
  "lowEarnersDetailsList": [
    {
      "taxYear": 2023,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2023-06-27",
            "claimStatus": "PENDING - CAPACITOR",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
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
    },
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
    }
  ]
}
```

### 'Deceased capacitor' details scenario:
```json
{
  "currentLowEarnersOptimisticLock": 90,
  "identifier": "STUB_NOT_USED",
  "lowEarnersDetailsList": [
    {
      "taxYear": 2023,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2023-06-27",
            "claimStatus": "DECEASED - CAPACITOR",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
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
    },
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
    }
  ]
}
```

### 'Deceased no capacitor' details scenario:
```json
{
  "currentLowEarnersOptimisticLock": 91,
  "identifier": "STUB_NOT_USED",
  "lowEarnersDetailsList": [
    {
      "taxYear": 2023,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2023-06-27",
            "claimStatus": "DECEASED - NO CAPACITOR",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
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
    },
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
    }
  ]
}
```

### 'Suspended' details scenario:
```json
{
  "currentLowEarnersOptimisticLock": 93,
  "identifier": "STUB_NOT_USED",
  "lowEarnersDetailsList": [
    {
      "taxYear": 2024,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2024-06-27",
            "claimStatus": "SUSPENDED - RLS",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
            "reissueClaimOutput": true,
            "reminderOutputSent": true
          },
          "lowEarnersDataDetails": {
            "calculationSequenceNumber": 123,
            "basicRatePercentage": 10.56,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "responseTimestamp": "2024-06-27 09:12:28",
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
            "claimStatus": "SUSPENDED - RLS",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
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
    },
    {
      "taxYear": 2025,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2025-06-27",
            "claimStatus": "SUSPENDED - RLS",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
            "reissueClaimOutput": true,
            "reminderOutputSent": true
          },
          "lowEarnersDataDetails": {
            "calculationSequenceNumber": 123,
            "basicRatePercentage": 10.56,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "responseTimestamp": "2025-06-27 09:12:28",
            "totalAllowances": 10.56,
            "totalDeductions": 10.56,
            "totalIncome": 10.56,
            "totalTaxDue": 10.56
          }
        }
      ]
    },
    {
      "taxYear": 2022,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2022-06-27",
            "claimStatus": "SUSPENDED - RLS",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
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
    }
  ]
}
```

### 'Underpayment' details scenario:
```json
{
  "currentLowEarnersOptimisticLock": 94,
  "identifier": "STUB_NOT_USED",
  "lowEarnersDetailsList": [
    {
      "taxYear": 2024,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "entitlementAmount": 10.56,
            "claimStatus": "PENDING",
            "inSelfAssessment": true,
            "calculationDate": "2024-06-27",
            "reminderOutputSent": true,
            "reissueClaimOutput": true
          },
          "lowEarnersDataDetails": {
            "responseTimestamp": "2024-06-27 09:12:28",
            "calculationSequenceNumber": 123,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "basicRatePercentage": 10.56,
            "totalAllowances": 10.56,
            "totalIncome": 10.56,
            "totalDeductions": 10.56,
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
            "entitlementAmount": 10.56,
            "claimStatus": "PAID",
            "inSelfAssessment": true,
            "calculationDate": "2023-06-27",
            "claimDate": "2023-06-27",
            "reminderOutputSent": true,
            "reissueClaimOutput": true
          },
          "lowEarnersDataDetails": {
            "responseTimestamp": "2023-06-27 09:12:28",
            "calculationSequenceNumber": 123,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "basicRatePercentage": 10.56,
            "totalAllowances": 10.56,
            "totalIncome": 10.56,
            "totalDeductions": 10.56,
            "totalTaxDue": 10.56
          }
        },
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "entitlementAmount": 10.56,
            "claimStatus": "PAID",
            "inSelfAssessment": true,
            "calculationDate": "2023-06-27",
            "claimDate": "2023-06-27",
            "reminderOutputSent": true,
            "reissueClaimOutput": true,
            "originalAmount": 10.56
          },
          "lowEarnersDataDetails": {
            "responseTimestamp": "2023-06-27 09:12:28",
            "calculationSequenceNumber": 123,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "basicRatePercentage": 10.56,
            "totalAllowances": 10.56,
            "totalIncome": 10.56,
            "totalDeductions": 10.56,
            "totalTaxDue": 10.56
          }
        }
      ]
    },
    {
      "taxYear": 2022,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "entitlementAmount": 10.56,
            "claimStatus": "PENDING",
            "inSelfAssessment": true,
            "calculationDate": "2022-06-27",
            "reminderOutputSent": true,
            "reissueClaimOutput": true,
            "originalAmount": 10.56
          },
          "lowEarnersDataDetails": {
            "responseTimestamp": "2022-06-27 09:12:28",
            "calculationSequenceNumber": 123,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "basicRatePercentage": 10.56,
            "totalAllowances": 10.56,
            "totalIncome": 10.56,
            "totalDeductions": 10.56,
            "totalTaxDue": 10.56
          }
        },
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "entitlementAmount": 10.56,
            "claimStatus": "PAID",
            "inSelfAssessment": true,
            "calculationDate": "2022-06-27",
            "claimDate": "2022-06-27",
            "reminderOutputSent": true,
            "reissueClaimOutput": true
          },
          "lowEarnersDataDetails": {
            "responseTimestamp": "2022-06-27 09:12:28",
            "calculationSequenceNumber": 123,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "basicRatePercentage": 10.56,
            "totalAllowances": 10.56,
            "totalIncome": 10.56,
            "totalDeductions": 10.56,
            "totalTaxDue": 10.56
          }
        }
      ]
    }
  ]
}
```

### 'Multiple payments' details scenario:
```json
{
  "currentLowEarnersOptimisticLock": 95,
  "identifier": "STUB_NOT_USED",
  "lowEarnersDetailsList": [
    {
      "taxYear": 2024,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2024-06-27",
            "claimStatus": "PENDING",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
            "reissueClaimOutput": true,
            "reminderOutputSent": true
          },
          "lowEarnersDataDetails": {
            "calculationSequenceNumber": 123,
            "basicRatePercentage": 10.56,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "responseTimestamp": "2024-06-27 09:12:28",
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
            "claimStatus": "PENDING",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
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
    },
    {
      "taxYear": 2025,
      "lowEarnersCalculations": [
        {
          "lowEarnersClaimDetails": {
            "claimSequenceNumber": 123,
            "calculationDate": "2025-06-27",
            "claimStatus": "PENDING",
            "entitlementAmount": 10.56,
            "inSelfAssessment": true,
            "reissueClaimOutput": true,
            "reminderOutputSent": true
          },
          "lowEarnersDataDetails": {
            "calculationSequenceNumber": 123,
            "basicRatePercentage": 10.56,
            "dataSourceMaster": "CESA",
            "netPayContributionsTotal": 10.56,
            "responseTimestamp": "2025-06-27 09:12:28",
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


## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

<br/>
