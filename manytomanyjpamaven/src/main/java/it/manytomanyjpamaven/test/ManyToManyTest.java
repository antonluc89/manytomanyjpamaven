package it.manytomanyjpamaven.test;

import java.util.Date;

import it.manytomanyjpamaven.dao.EntityManagerUtil;
import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.StatoUtente;
import it.manytomanyjpamaven.model.Utente;
import it.manytomanyjpamaven.service.MyServiceFactory;
import it.manytomanyjpamaven.service.RuoloService;
import it.manytomanyjpamaven.service.UtenteService;

public class ManyToManyTest {

	public static void main(String[] args) {
		UtenteService utenteServiceInstance = MyServiceFactory.getUtenteServiceInstance();
		RuoloService ruoloServiceInstance = MyServiceFactory.getRuoloServiceInstance();

		// ora passo alle operazioni CRUD
		try {

			// inizializzo i ruoli sul db
			initRuoli(ruoloServiceInstance);

			System.out.println("Elenca utenti: ");
			for (Utente utenteItem : utenteServiceInstance.listAll()) {
				System.out.println(utenteItem);
			}

			Utente utenteNuovo = new Utente("mario.rossi", "xxx", "Mario", "Rossi", new Date());
			utenteServiceInstance.inserisciNuovo(utenteNuovo);

			Ruolo ruoloDaDb = ruoloServiceInstance.caricaSingoloElemento(1L);

			Utente utenteDaDb = utenteServiceInstance.listAll().stream().findFirst().orElse(null);
			if (utenteDaDb != null) {
				utenteServiceInstance.aggiungiRuolo(utenteDaDb, ruoloDaDb);
			}

			System.out.println("*********** PROVA LIST RUOLO ***********");
			for (Ruolo ruoloItem : ruoloServiceInstance.listAll()) {
				System.out.println(ruoloItem);
			}

			System.out.println("*********** PROVA INSERT NUOVO RUOLO ***********");
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Guest", "ROLE_GUEST"));

			System.out.println("*********** PROVA DELETE UTENTE ***********");
			Utente utenteDaRimuovereDalDb = utenteServiceInstance.caricaSingoloElemento(4L);
			utenteServiceInstance.rimuovi(utenteDaRimuovereDalDb);

			System.out.println("*********** PROVA DELETE RUOLO ***********");
			Ruolo ruoloDaRimuovereDalDb = ruoloServiceInstance.caricaSingoloElemento(3L);
			ruoloServiceInstance.rimuovi(ruoloDaRimuovereDalDb);

			System.out.println("*********** PROVA UPDATE UTENTE ***********");
			Utente toBeUptaded = utenteServiceInstance.caricaSingoloElemento(5L);
			toBeUptaded.setCognome("Verdi");
			utenteServiceInstance.aggiorna(toBeUptaded);

			System.out.println("*********** PROVA UPDATE RUOLO ***********");
			Ruolo ruoloDaAggiornare = ruoloServiceInstance.caricaSingoloElemento(5L);
			ruoloDaAggiornare.setCodice("ROLE_GUEST_USER");
			ruoloDaAggiornare.setDescrizione("Guest User");
			ruoloServiceInstance.aggiorna(ruoloDaAggiornare);

			System.out.println("*********** PROVA FIND ALL UTENTI BY RUOLO ***********");
			Ruolo ruoloPerRicerca = ruoloServiceInstance.caricaSingoloElemento(2L);
			for (Utente utenteByRuolo : utenteServiceInstance.cercaTuttiGliUtentiByRuolo(ruoloPerRicerca)) {
				System.out.println(utenteByRuolo);
			}

			System.out.println("*********** PROVA RUOLO BY CODICE E DESCRIZIONE ***********");
			Ruolo ruoloDaTrovare = ruoloServiceInstance.cercaPerDescrizioneECodice("Unusual User", "ROLE_UNUSUAL_USER");
			System.out.println(ruoloDaTrovare.getId());

			// proviamo a passarlo nello stato ATTIVO
			Utente utenteDaDb2 = utenteServiceInstance.listAll().stream().findFirst().orElse(null);
			if (utenteDaDb2 != null) {
				System.out.println(
						"stato attuale dell'utente :" + utenteDaDb2.getUsername() + " " + utenteDaDb2.getStato());
				utenteDaDb2.setStato(StatoUtente.ATTIVO);
				utenteServiceInstance.aggiorna(utenteDaDb2);
				System.out.println(
						"stato nuovo dell'utente :" + utenteDaDb2.getUsername() + " " + utenteDaDb2.getStato());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// questa è necessaria per chiudere tutte le connessioni quindi rilasciare il
			// main
			EntityManagerUtil.shutdown();
		}

	}

	private static void initRuoli(RuoloService ruoloServiceInstance) throws Exception {
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", "ROLE_ADMIN"));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", "ROLE_CLASSIC_USER") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Classic User", "ROLE_CLASSIC_USER"));
		}
	}

}
