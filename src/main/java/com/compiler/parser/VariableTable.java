package com.compiler.parser;

import com.compiler.utils.MiniWriter;
import com.compiler.utils.PathConfig;

import java.util.ArrayList;

public class VariableTable {
    private ArrayList<VariableEntry> variables;

    public enum VariableType {
        PARAMETER,
        VARIABLE
    }

    public static class VariableEntry {
        String vname;
        String vproc;
        VariableType vkind;
        String vtype;   // "integer"
        int vlev;
        int vadr;       // index in globalVarTable

        public VariableEntry(String vname, String vproc, VariableType vkind, String vtype, int level) {
            this.vname = vname;
            this.vproc = vproc;
            this.vkind = vkind;
            this.vtype = vtype;
            this.vlev = level;
        }

        public String print() {
            return vname + " " + vproc + " " + vkind + " " + vtype + " " + vlev;
        }
    }

    public VariableTable() {
        variables = new ArrayList<VariableEntry>(24);
    }

    /**
     * add a new VariableEntry to table
     * @param vname name
     * @param vproc belong to ehich procedure
     * @param vkind type of variable, VariableTable.VariableType
     * @param vtype name of vkind
     * @param level belong to which level
     * @return the position of newly added entry in table
     */
    public int add(String vname, String vproc, VariableType vkind, String vtype, int level) {
        var entry = new VariableEntry(vname, vproc, vkind, vtype, level);
        int pos = variables.size();
        entry.vadr = pos;
        variables.add(entry);
        return pos;
    }

    public int add(VariableEntry v) {
        int pos = variables.size();
        variables.add(v);
        return pos;
    }

    public boolean contains(String vname, int level) {
        return variables.stream()
                .anyMatch(entry ->
                        entry.vname.equals(vname) &&
                                entry.vlev <= level
                        // has defined a same var
                );
    }

    public boolean delete(String vname) {
        return variables.removeIf(entry ->
                entry.vname.equals(vname));
    }

    public void clear() {
        variables.clear();
    }

    public void print() {
        MiniWriter writer = new MiniWriter(PathConfig.getOutputVar());
        for (VariableEntry entry : variables) {
            writer.write(entry.print());
        }
    }
}
