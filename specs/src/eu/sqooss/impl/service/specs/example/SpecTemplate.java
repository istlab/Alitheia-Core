package eu.sqooss.impl.service.specs.example;

import java.util.Collection;
import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class SpecTemplate
{
    public String status()
    {
        return "connected";
    }

    public Collection<Person> getPeople()
    {
        return new ArrayList<Person>() {
        	public static final long serialVersionUID = -1;
        	{
            add(new Person("Peter", "Gibbons"));
            add(new Person("Samir", "Nagheenanajar"));
            add(new Person("Michael", "Bolton"));
            add(new Person("Milton", "Waddams"));
        	}
        };
    }

    class Person
    {

        public Person(String firstName, String lastName)
        {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String firstName;
        public String lastName;
    }
}
