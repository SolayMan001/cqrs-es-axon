package ma.oumouou.cqrsesaxon.config;

import ma.oumouou.cqrsesaxon.commands.commands.AccountCommandError;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfiguration {
    @Bean
    ConfigurerModule accountProjectionErrors() {
        return configurer -> configurer.eventProcessing()
                .registerListenerInvocationErrorHandler("accounts", configuration -> PropagatingErrorHandler.instance());
    }

    @Bean
    SmartInitializingSingleton accountCommandErrors(CommandBus commandBus) {
        return () -> commandBus.registerHandlerInterceptor((unitOfWork, chain) -> {
            try {
                return chain.proceed();
            } catch (AggregateNotFoundException exception) {
                throw new CommandExecutionException(exception.getMessage(), exception, AccountCommandError.ACCOUNT_NOT_FOUND);
            } catch (IllegalArgumentException exception) {
                throw new CommandExecutionException(exception.getMessage(), exception, AccountCommandError.INVALID_COMMAND);
            }
        });
    }
}
