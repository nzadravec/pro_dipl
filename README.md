# [Diplomski projekt](https://www.fer.unizg.hr/predmet/pro_dipl) (FER) - Sustav za poluautomatsko označavanje rukom pisanih riječi

## Kratak opis teme projekta:
<p align="justify">
Tema projekta je izrada sustav za poluautomatsko označavanje rukom pisanih 
riječi te uporaba istoga za pripremu odgovarajućeg skupa podataka.
Ulaz u sustav će biti skenirani obrasci na kojima će biti rukom pisane riječi
štampanim slovima. Sustav će na svakom obrascu lokalizirati riječi, 
segmentirati slova pojedinih riječi te uporabom jednostavnog klasifikatora označiti
svako slovo. Pomoću grafičkog sučelja korisnik će moći vidjeti i ispraviti 
okvir oko slova i oznaku slova.
</p>

## Opis programskog rješenja
<p align="justify">
Izrađen je sustav za poluautomatsko označavanje rukom pisani teksta štampanim slovima. Radi se o jednostavnom grafičkom sučelju pomoću kojeg se može brzo anotirati pisani tekst. Sustav ima mogućnost anotirati slova, riječi te linije teksta.
Ulaz u sustav su slika skeniranog papira na kojem je rukom pisan tekst štampanim slovima te tekstualna datoteka (.txt) koja sadrži tekst koji je bio rukom pisan. Za papir na kojem se rukom pisao tekst pretpostavlja se da je običan bijeli papir, a za tekstualnu datoteku se pretpostavlja da redovi teksta prate pisane redove, odnosno, da tekstualna datoteka prati rukom pisani tekst ne samo po riječima koje su bile pisane, nego da se te riječi nalaze u odgovarajućem redu u kojem se nalaze i u pisanom tekstu.
Sustav ima mogućnost automatski označiti granični okvir (bounding box) oko svake povezane komponente koja se nalazi u slici. Pretpostavlja se da će se tako većina pisanih
štampanih slova označiti. U slučaju da određeni granični okvir obuhvaća samo dio slova ili pak obuhvaća više od jednog slova (ako se slova dodiruju), sustav omogućava niz operacija nad graničnim okvirima pomoću kojih ih korisnik može ispraviti. Operacije su sljedeće: preoblikovanje (promjena visine, širini itd.), brisanje, razdvajanje graničnog okvira pravcem, definiranje novog graničnog okvira te spajanje više okvira u jedan.
Nakon što su označeni granični okviri povezanih komponenata sustav ih može automatski povezati (grupirati) u linije tako da svaki granični okvir pripada jednoj liniji. Svaka linija sadrži one granične okvire za koje sustav smatra da označavaju slova jedne linije teksta. Ako se granični okviri ne bi dobro povezali u linije, sustav podržava operacije 
pomoću kojih korisnik to može ispraviti. Jedna od operacija povezuje granične okvire u novu liniju, a druga operacija dodaje granični okvir u već definiranu liniju ili veže dvije linije u jednu.
Nakon što su granični okviri povezani u linije, sustav može automatski povezati granične okvire u jednoj liniji u riječi. U slučaju da su granični okviri u jednoj liniji nisu dobro grupirani u riječi, sustav podržava operaciju pomoću koje korisnik to može ispraviti.
Zadnji korak u označavanju je poravnanje označenih riječi i riječima u tekstualnoj datoteci. Ako je označena riječ sastavljena od više graničnih okvira nego što riječ na iste mjestu u tekstualnoj datoteci ima znakova, grafičko sučelje će to istaknuti korisniku. U tom slučaju korisnik treba ili spojiti granične okvire koji čine jedan znak u jedan okvir ili razdvojiti okvir koji obuhvaća više od jednog znaka.
Izlaz iz sustava je xml datoteka koja sadrži informacije o anotiranim slovima, riječima i linijama teksta.
</p>
