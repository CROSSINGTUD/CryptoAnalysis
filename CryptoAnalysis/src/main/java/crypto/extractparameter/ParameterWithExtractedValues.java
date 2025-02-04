package crypto.extractparameter;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import java.util.Collection;

public record ParameterWithExtractedValues(
        Statement statement,
        Val param,
        int index,
        String varName,
        Collection<ExtractedValue> extractedValues) {}
