package eu.sqooss.impl.service.messaging.senders.smtp.connection;

/**
 * This interface is for factories for SASLs.
 */
public interface SASLFactory {
    /**
     * Returns all available SASL mechanisms in this factory.
     * @return an array of SASL mechanisms. MUST NOT return null
     */
    public SASL[] getAvailableSASLs();

    /**
     * Returns the SASL mechanism having the specified ID
     * @param id the ID ot he mechanism. same as the one sent in AUTH commands.
     * @return an SASL implementation or null if a mechanism with the given ID
     * does not exist.
     */
    public SASL getSASL(String id);
}
