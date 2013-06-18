package org.craftercms.services.api;

/**
 * Greeting Services Definition.
 */
public interface GreetingService {

    /**
     * Greets a user given his name
     * @param name User's name
     * @return a custom greet for the user
     */
    String greet(String name);
}
