import java.util.*;

class Taikurikoulu{

    public static Taikuri koulutaUusiTaikuri(){
    	Taikuri taikapasi = new Taikuri(100000);
        return taikapasi;
    }
    
	static class Taikuri extends Henkilo implements TarinanSankari,Loitsiva{
	    
	    private boolean suojattu = false;
	    
	    public Taikuri(int elama){
	        super(elama);
	    }
	    
	    @Override
	    public void loitsi(Loitsu t) {
	        t.taio();
	    }
	    
	    @Override
	    protected void reagoiLoitsuun(Loitsu l) {
	        if (l instanceof Suojaloitsu) {
	            suojattu = true;
	        }
	        
	        if (l instanceof Vahingoittava) {
	        	System.out.println("ZZZzap!");
	        	loitsi(new Tappoloitsu(((Loitsiva)this), ((Tahdattava)l.getLahde())));
	        }
	        
	        if (suojattu && l.getLahde() instanceof Velho) {
	            throw new TaikaPoikkeus(
	                    "Suoja pys‰ytt‰‰ velhon taian");
	        }
	    }
	    
	    @Override
	    public void suoritaVuoro(int vuoro){
	        switch (vuoro) {
	        case 0:
	            loitsi(new Suojaloitsu(this, this));
	            break;
	        case 1:
	            loitsi(new Suojaloitsu(this, this));
	            break;
	        case 2:
	        	//reagoi tappoloitsuun
	        }
	    }
	    
	    private class Tappoloitsu extends Loitsu implements Vahingoittava {
	
	        public Tappoloitsu(Loitsiva lahde, Tahdattava kohde) {
	            super(lahde, kohde);
	        }
	    
	        @Override
	        public int getVahinko() {
	            if (getKohde() instanceof Velho) {
	                return ((Kuoleva) getKohde()).annaElama();
	            }
	            return ((Kuoleva) getKohde()).annaElama();
	        }
	    }
	
	    
	    private class Suojaloitsu extends Loitsu {
	
	        public Suojaloitsu(Loitsiva lahde, Tahdattava kohde) {
	            super(lahde, kohde);
	        }
	        
	    }
	    
	}

}


/**
 * ----------------LUOKAT--------------
 */
public class Test {

    public static void main(String[] args) {
        TarinanSankari t = Taikurikoulu.koulutaUusiTaikuri();

        if (!(t instanceof Loitsiva)) {
            System.out.println("Taikurikokelaasi ei osaa loitsia"
                    + ", ja velho nujersi h‰net saman tien!");
            return;
        }

        Velho v = new Velho(500, t);

        for (int i = 0; i < 3; i++) {
            t.suoritaVuoro(i);
            v.suoritaVuoro(i);
        }

        Henkilo taikuri = (Henkilo) t;
        if (taikuri.annaElama() <= 0) {
            System.out.println("Velho nujersi sinut!");
        } else {
            System.out.println("Velho ei onnistunut nujertamaan sinua");
        }

        if (v.annaElama() <= 0) {
            System.out.println("Onnistuit nujertamaan velhon!");
        } else {
            System.out.println("Velho j‰i viel‰ henkiin.");
        }
    }
}

abstract class Henkilo implements Tahdattava, Kuoleva {
    //Attribuutti on private, jotta teht‰v‰ olisi vaikeampi...
    private int elama;

    public Henkilo(int elama) {
        this.elama = elama;
    }

    @Override
    public int annaElama() {
        return elama;
    }

    private void muutaElamaa(int muutos) {
        elama += muutos;
    }

    @Override
    public void vastaanota(Loitsu l) {

        try {
            reagoiLoitsuun(l);

            if (l instanceof Vahingoittava) {
                muutaElamaa(-((Vahingoittava) l).getVahinko());
            }

            if (l instanceof Parantava) {
                muutaElamaa(((Parantava) l).getParannuksenMaara());
            }
        } catch (TaikaPoikkeus e) {
            e.selvitaTaikapoikkeus();
        }
    }

    protected abstract void reagoiLoitsuun(Loitsu l);

    public abstract void suoritaVuoro(int vuoro);
}

/**
 * Vaarallinen velho, joka sinun tulee voittaa kaksintaistelussa.
 */
class Velho extends Henkilo implements Loitsiva {

    private TarinanSankari taikuri;
    private boolean suojattu = false;

    public Velho(int elama, TarinanSankari taikuri) {
        super(elama);
        this.taikuri = taikuri;
    }

    @Override
    protected void reagoiLoitsuun(Loitsu l) {
        if (l instanceof Suojaloitsu) {
            suojattu = true;
        }

        if (suojattu && l.getLahde() instanceof TarinanSankari) {
            throw new TaikaPoikkeus(
                    "Velho on taikonut suojaloitsun; taikasi eiv‰t tehoa velhoon.");
        }
    }

    @Override
    public void loitsi(Loitsu t) {
        t.taio();
    }

    @Override
    public void suoritaVuoro(int vuoro) {
        switch (vuoro) {
        case 0:
            loitsi(new Suojaloitsu(this, this));
            break;
        case 1:
            loitsi(new Parannusloitsu(this, this));
            break;
        case 2:
            loitsi(new Tappoloitsu(this, taikuri));
        }
    }

    /*
     *  Velho osaa kolme loitsua.
     */
    private class Tappoloitsu extends Loitsu implements Vahingoittava {

        public Tappoloitsu(Loitsiva lahde, Tahdattava kohde) {
            super(lahde, kohde);
        }

        @Override
        public int getVahinko() {
            if (getKohde() instanceof Kuoleva) {
                return ((Kuoleva) getKohde()).annaElama();
            }
            return 0;
        }
    }

    private class Parannusloitsu extends Loitsu implements Parantava {

        public Parannusloitsu(Loitsiva lahde, Tahdattava kohde) {
            super(lahde, kohde);
        }

        @Override
        public int getParannuksenMaara() {
            return 100;
        }

    }

    private class Suojaloitsu extends Loitsu {

        public Suojaloitsu(Loitsiva lahde, Tahdattava kohde) {
            super(lahde, kohde);
        }

    }
}

abstract class Loitsu {
    // Attributtit ovat private, koska jokainen konkreettinen loitsu tiet‰‰ vain
    // oman toimintansa. Taikojen toiminta ei yleens‰ ole riippuvainen
    // taikojasta tai kohteesta.
    // Havainnointimetodit ovat kuitenkin k‰ytˆss‰, jos jokin taika todella
    // tarvitsee t‰t‰ tietoa
    private Loitsiva lahde;
    private Tahdattava kohde;

    public Loitsu(Loitsiva lahde, Tahdattava kohde) {
        super();
        this.lahde = lahde;
        this.kohde = kohde;
    }

    public Tahdattava getKohde() {
        return kohde;
    }

    public Loitsiva getLahde() {
        return lahde;
    }

    public void taio() {
        kohde.vastaanota(this);
    }
}

/**
 * ----------------RAJAPINNAT--------------
 */

// Rajapinta kuoleville olioille
interface Kuoleva {
    int annaElama();
}

// Rajapinta t‰hd‰tt‰ville olioille
interface Tahdattava {
    void vastaanota(Loitsu l);
}

// Rajapinta olioille, jotka osaavat loitsia
interface Loitsiva {
    void loitsi(Loitsu l);
}

// Rajapinta olioille, jotka pystyv‰t vahingoittamaan.
interface Vahingoittava {
    int getVahinko();
}

// Rajapinta olioille, jotka voivat parantaa
interface Parantava {
    int getParannuksenMaara();
}

// Jos olio voi olla tarinan sankari, sen tulee toteuttaa t‰m‰ rajapinta.
interface TarinanSankari extends Tahdattava {
    void suoritaVuoro(int vuoro);
}

/**
 * ----------------POIKKEUKSET--------------
 */
class TaikaPoikkeus extends RuntimeException {

    public TaikaPoikkeus(String arg0) {
        super(arg0);
    }

    public void selvitaTaikapoikkeus() {
        System.out.println(getMessage());
    }
}