package ua.com.serverhelp.simplemonitoring.api.trigger;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import ua.com.serverhelp.simplemonitoring.entity.alert.inputtype.IAlerter;
import ua.com.serverhelp.simplemonitoring.entity.triggers.Trigger;
import ua.com.serverhelp.simplemonitoring.entity.triggers.inputtype.ITrigger;
import ua.com.serverhelp.simplemonitoring.repository.*;
import ua.com.serverhelp.simplemonitoring.rest.exceptions.AccessDeniedError;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TriggerController {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final TriggerRepository triggerRepository;

    @QueryMapping
    public Page<Trigger> triggers(@Argument UUID orgId, @Argument Integer page, @Argument Integer size, Authentication authentication){
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(orgId, user).orElseThrow();

        return triggerRepository.findAllByOrganization(org, PageRequest.of(page, size));

    }

    @MutationMapping
    public Trigger updateTrigger(@Argument Long triggerId, @Argument ITrigger inputTrigger, Authentication authentication){
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(inputTrigger.getOrganizationId(), user).orElseThrow();
        var trigger = triggerRepository.findById(triggerId).orElseThrow();
        if (trigger.getOrganization().getId().equals(org.getId())) {
            trigger.setName(inputTrigger.getName());
            trigger.setDescription(inputTrigger.getDescription());
            trigger.setTriggerId(inputTrigger.getTriggerId());
            trigger.setConf(inputTrigger.getConf());
            trigger.setEnabled(inputTrigger.getEnabled());
            trigger.setMuted(inputTrigger.getMuted());
            trigger.setSuppressedScore(inputTrigger.getSuppressedScore());
            trigger.setPriority(inputTrigger.getPriority());

            return triggerRepository.save(trigger);
        }
        throw new AccessDeniedError("Access denied");
    }

    @MutationMapping
    public String deleteTrigger(@Argument Long triggerId, Authentication authentication){
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var trigger = triggerRepository.findById(triggerId).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(trigger.getOrganization().getId(), user).orElseThrow();
        triggerRepository.delete(trigger);
        return "Success";
    }
}
