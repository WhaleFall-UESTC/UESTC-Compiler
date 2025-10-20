package com.compiler.parser;

import com.compiler.utils.MiniLogger;
import com.compiler.utils.MiniWriter;
import com.compiler.utils.PathConfig;

import java.util.ArrayList;
import java.util.List;

public class ProcedureTable {

    private ArrayList<ProcedureEntry> procedures;

    public static class ProcedureEntry {
        String pname;
        String ptype;   // "integer"
        int plev;
        int fadr;       // first var index in globalVarTable
        int ladr;       // last var index (inclusive)

        ProcedureEntry(String pname, String ptype, int plev) {
            this.pname = pname;
            this.ptype = ptype;
            this.plev = plev;
        }

        void setAdr(int fadr, int ladr) {
            this.fadr = fadr;
            this.ladr = ladr;
        }

        /**
         * print as *.pro format
         * @return a line in *.pro
         */
        String print() {
            return pname + " " + ptype + " " + plev + " " + fadr + " " + ladr;
        }
    }

    public ProcedureTable() {
        procedures = new ArrayList<ProcedureEntry>();
    }

    public int add(String pname, String ptype, int plev) {
        int pos =  procedures.size();
        procedures.add(new ProcedureEntry(pname, ptype, plev));
        return pos;
    }

    public void procedureAddAdr(int index, int fadr, int ladr) {
        if (index < 0 || index >= procedures.size()) {
            MiniLogger.error("Procedure index out of bounds!");
            throw new IllegalArgumentException("Invalid procedure index: " + index);
        }
        procedures.get(index).setAdr(fadr, ladr);
    }

    public boolean isLegalIndex(int index) {
        return index >= 0 && index < procedures.size();
    }

    public boolean contains(String pname, int plev) {
        return procedures.stream()
                .anyMatch(entry ->
                        entry.pname.equals(pname) &&
                                entry.plev <= plev
                        // has defined a same function at higher or the same level
                );
    }

    public String getProcedureName(int index) {
        return procedures.get(index).pname;
    }

    public void print() {
        MiniWriter writer = new MiniWriter(PathConfig.getOutputPro());
        for (ProcedureEntry entry : procedures) {
            writer.write(entry.print());
        }
    }
}
