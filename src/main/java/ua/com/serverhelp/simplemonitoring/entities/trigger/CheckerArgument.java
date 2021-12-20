package ua.com.serverhelp.simplemonitoring.entities.trigger;

import ua.com.serverhelp.simplemonitoring.entities.parametergroup.IParameterGroup;

public interface CheckerArgument {
    IParameterGroup getParameterGroup();
    int getPosition();
}
