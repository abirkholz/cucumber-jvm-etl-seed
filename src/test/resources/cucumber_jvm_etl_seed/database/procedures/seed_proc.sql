CREATE PROCEDURE dbo.MergeData_snlRegFilings_prc
  AS
  INSERT INTO dbo.snlRegFilings (KeyDoc, KeyBankRegFinlEOP, FilingDate, KeyInstnFilingType, InstnFilingType)
    SELECT KeyDoc, KeyBankRegFinlEOP, FilingDate, KeyInstnFilingType, InstnFilingType
    FROM dbo.BankRegFinlEOPFiling
