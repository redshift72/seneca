package it.enea.lecop.eca.model;


/**
 * ci dice il contesto di applicazione dei permessi
 * 
 * OWNUSER quando l'utente attuale  corrisponde con l'utente proprietario
 * IDENTITYDOMAIN  quando  company domain dell'utente attuale coincide con il company domain dell'oggetto
 * SUBSETDOMAIN     quando  il company domain dell'utente contiene strettamente (e più grande) il company domain dell' oggetto
 * INTERSECTIONDOMAIN  quando l'insieme dei compamy domain ha almeno un'azienda in comune (intersezione non nulla) con il company domain dell'oggetto 
 * OTHER quando l'utente attuale non è il prorpietario dell'ogggetto e il suo company domain ha un intersezione nulla
 * @author fab
 *
 */
public enum NameSecContext {
	OWNERUSER,IDENTITYDOMAIN,SUBSETDOMAIN,INTERSECTIONDOMAIN,OTHER
}
