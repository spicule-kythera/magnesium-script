package uk.co.spicule.magnesium_script.expressions;

import jdk.nashorn.internal.objects.annotations.Getter;
import java.util.List;

public interface Subroutine {
    @Getter List<String> getFlatStack();
}