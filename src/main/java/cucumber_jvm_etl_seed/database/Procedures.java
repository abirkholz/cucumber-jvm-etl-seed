package cucumber_jvm_etl_seed.database;


public class Procedures {

    private static final String MergeData_snlRegFilings_prc = "{call MergeData_snlRegFilings_prc}";
    private static final String mssql_full_pull_proc = "{call mssql_full_pull_proc}";
    private static final String psql_demo_procedure = "{call psql_demo_procedure(?,?,?)}";
    private static final String mssql_demo_proc = "{call mssql_demo_proc(?,?,?,?)}";

    public static String getProcedure(String procedure) {
        switch (procedure) {
            case "seed_proc": return MergeData_snlRegFilings_prc;
            case "mssql_full_pull_proc": return mssql_full_pull_proc;
            case "psql_demo_procedure": return psql_demo_procedure;
            case "mssql_demo_proc": return mssql_demo_proc;
            default: return null;
        }
    }
}
