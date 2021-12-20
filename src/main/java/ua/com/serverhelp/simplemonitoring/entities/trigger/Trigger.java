package ua.com.serverhelp.simplemonitoring.entities.trigger;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.CheckTriggerException;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;
import ua.com.serverhelp.simplemonitoring.utils.MetricUnreachableException;

import javax.persistence.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Trigger{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    @Type(type = "text")
    private String checkerClass;
    @Getter
    @Setter
    private String host;

    public boolean checkState(Storage storage) throws CheckTriggerException, MetricUnreachableException {
        try {
            Class<?> c = Class.forName(checkerClass);
            Constructor<?> cons = c.getConstructor();
            Checker checker = (Checker) cons.newInstance();
            List<? extends CheckerArgument> checkerArguments=storage.getCheckerArgumentsByTrigger(this);
            return checker.checkState(checkerArguments,storage);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new CheckTriggerException("ParameterGroupTrigger::checkState "+e.getLocalizedMessage());
        }
    }

    public String getName() {
        try {
            Class<?> c = Class.forName(checkerClass);
            Constructor<?> cons = c.getConstructor();
            Checker checker = (Checker) cons.newInstance();
            return checker.getName();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            MYLog.printError("ParameterGroupTrigger::getName can not load class "+checkerClass,e);
        }
        return "";
    }

    public String getDescription() {
        try {
            Class<?> c = Class.forName(checkerClass);
            Constructor<?> cons = c.getConstructor();
            Checker checker = (Checker) cons.newInstance();
            return checker.getDescription();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            MYLog.printError("ParameterGroupTrigger::getName can not load class "+checkerClass,e);
        }
        return "";
    }
}
