package crypto.rules;

import java.util.ArrayList;
import java.util.List;

public class CrySLValueConstraint extends CrySLLiteral {

    CrySLObject var;
    List<String> valueRange;

    public CrySLValueConstraint(CrySLObject name, List<String> values) {
        var = name;
        valueRange = values;
    }

    /**
     * @return the varName
     */
    public String getVarName() {
        return var.getVarName();
    }

    /**
     * @return the varName
     */
    public CrySLObject getVar() {
        return var;
    }

    /**
     * @return the valueRange
     */
    public List<String> getValueRange() {
        return valueRange;
    }

    public String toString() {
        StringBuilder vCSB = new StringBuilder();
        vCSB.append("VC:");
        vCSB.append(var);
        vCSB.append(" - ");
        for (String value : valueRange) {
            vCSB.append(value);
            vCSB.append(",");
        }
        return vCSB.toString();
    }

    @Override
    public List<String> getInvolvedVarNames() {
        List<String> varNames = new ArrayList<>();
        varNames.add(var.getVarName());
        return varNames;
    }

    @Override
    public String getName() {
        return toString();
    }
}
