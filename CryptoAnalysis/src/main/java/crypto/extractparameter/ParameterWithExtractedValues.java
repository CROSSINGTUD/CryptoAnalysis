package crypto.extractparameter;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;

public record ParameterWithExtractedValues(
        Statement statement,
        Val param,
        int index,
        String varName,
        Collection<ExtractedValue> extractedValues) {}
