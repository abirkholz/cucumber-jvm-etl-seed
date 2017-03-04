Feature: Database testing demo exercising JDBC capabilities

  @db_demo
  Scenario Outline: Verify source and target tables have the same table schema
    Given a connection to environment "<environment>" with database "<database>"
    When I query the database for the "<source_table>" schema
    And I query the database for the "<target_table>" schema
    Then the "<source_table>" schema should match the "<target_table>" schema

    Examples:
    | environment   | database | source_table | target_table |
    | postgres      | demo     | source_tbl_a | target_tbl_a |
    | postgres      | demo     | source_tbl_b | target_tbl_b |


  @pull_merge @snlRegFilings
  Scenario Outline: Verify the snlRegFilings procedure
    Given a connection to environment "<environment>" with database "<database>"
    When I run "insert" queries from file "seed_test_data.xml" against source table "BankRegFinlEOPFiling"
    And I run "update" queries from file "seed_test_data.xml" against source table "BankRegFinlEOPFiling"
    And I run "delete" queries from file "seed_test_data.xml" against source table "BankRegFinlEOPFiling"
    And I execute the stored procedure "seed_proc"
    Then target table "snlRegFilings" should receive the "insert" rows from source table "BankRegFinlEOPFiling"
    And target table "snlRegFilings" should receive the "update" rows from source table "BankRegFinlEOPFiling"
    And target table "snlRegFilings" should not receive the deleted rows from source table "BankRegFinlEOPFiling"

    Examples:
      | environment  | database |
      | mssql-docker | demo     |