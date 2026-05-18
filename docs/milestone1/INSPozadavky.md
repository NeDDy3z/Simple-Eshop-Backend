FR-01 Fixace kupní ceny	
Při vytvoření objednávky musí systém zafixovat aktuální cenu produktů. 
Případné budoucí zdražení nesmí ovlivnit historii objednávek.

FR-02 Kategorizace a vyhledávání
Systém umožní filtrovat produkty podle kategorií.

FR-03 Aplikace slev	
Zákazníkům může být při nákupu aplikována sleva. 
Systém musí podporovat minimálně dva typy: fixní částku a procentuální slevu.

FR-04 Fasáda pro nákupní proces	
Celý proces dokončení objednávky musí být zapouzdřen do jedné operace, aby se zabránilo nekonzistenci dat.

FR-05 Rozlišení rolí	
Systém musí rozlišovat přístupová práva pro běžného zákazníka a administrátora.

NFR-01 Rozšiřitelnost strategií 
Systém musí umožnit přidání nového typu slevy (např. sezónní výprodej) bez nutnosti modifikace stávající třídy Order.

NFR-02 Bezstavovost
Aplikační vrstva nesmí ukládat stav relace v paměti instance. Veškerá data musí být v DB nebo sdílené cache kvůli Load Balanceru.

NFR-03 Volná vazba
Vedlejší procesy (e-mail, Kafka) musí běžet asynchronně přes události. Výpadek e-mailového serveru nesmí zablokovat nákup.

NFR-05 Optimalizace výkonu
Často čtená data (kategorie produktů) musí být dostupná přes in-memory cache (Hazelcast) pro minimalizaci dotazů do DB.

FR-06 Správa produktů
Systém umožní administrátorovi přidávat nové produkty do katalogu a odebírat existující produkty ze systému prostřednictvím zabezpečeného rozhraní.