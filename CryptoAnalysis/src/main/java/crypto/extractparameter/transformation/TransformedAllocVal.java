package crypto.extractparameter.transformation;

import boomerang.scene.AllocVal;
import boomerang.scene.Statement;
import boomerang.scene.Val;

public class TransformedAllocVal extends AllocVal {

    public TransformedAllocVal(Val delegate, Statement allocStatement, Val allocationVal) {
        super(delegate, allocStatement, allocationVal);
    }
}
