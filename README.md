# Cassandra-MMO-Bots
Cassandra-MMO-Bots jest programem napisanym w Javie i wykorzystuje bazę danych Cassandra do symulacji działania sieci botów, które zbierają złoto w grze MMO.

##### W symulacji zostały wyróżnione trzy typu obiektów:
- **bot** - osobny wątek, który symuluje działanie bota. Każdy bot ma zdefiniowane te same parametry dotyczące poruszania się i pozyskiwania złota:
  - **limit złota w plecaku** - limit złota, które bot może dźwigać w danym momencie. Aby opróżnić plecak, bot musi udać się do miasta.
  - **prędkość zbierania** - ilość złota jaką bot może zebrać w czasie 1 sekundy.
  - **prędkość podróżowania** - liczba przebytych "kroków" na mapie, jaką bot jest wstanie wykonać w czasie 1 sekundy.
  - **limit czasu** - limit czasu działania symulacji w sekundach.
  - **czas oczekiwania** - czas oczekiwania na synchronizację cassandry w sekundach.
- **miejsce z zasobami** - punkt na mapie z ograniczoną pulą złota, z której boty mogą pozyskiwać złoto do plecaka. Zasoby złota nie są odnawiane, dlatego gdy pula się skończy, miejsce to nie będzie już odwiedzane przez boty. W każdym miejscu, może przebywać jednocześnie tylko jeden bot. Miejsce nie wpływa na prędkość zbierania złota, natomiast bot nie może zebrać więcej złota niż pozostało w puli.
- **miasto** - punkt,  w którym boty mogą odnieść zebrane złoto aby zwolnić miejsce w plecaku. W mieście może przebywać jednocześnie nieograniczona liczba botów.

##### Działanie bota

Bot zaczyna w losowym miejscu na mapie z pustym plecakiem oraz listą miejsc, które mógłby odwiedzić. Nie posiada on informacji na temat innych botów oraz ich liczby. W momencie, gdy chce zacząć zbierać złoto, pobiera wszystkie logi opisujących wydobyte złoto z poszczególnych miejsc. Za pomocą tych danych, bot jest wstanie obliczyć złoto jakie pozostało w każdym miejscu. Następnie na podstawie tych informacji oraz swojego położenia, tworzy posortowaną listę miejsc do odwiedzenia. Lista jest posortowana według obliczonego kryterium określającego stosunek wydobytego złota do poświęconego czasu. Następnie bot próbuje zastrzec sobie to miejsce dla siebie i oczekuje odpowiednią liczbę sekund, aby mogło dojść do synchronizacji nodów bazy danych. Następnie sprawdza czy jako pierwszy zarezerwował miejsce. Jeżeli nie, to bot rezygnuje z miejsca na rzecz drugiego bota i próbuje ponownie z kolejnym miejscem w liście. Jeżeli mu się udało, to udaje się w stronę określonego miejsca i zbiera w nim złoto. Po zebraniu złota, bot informuje bazę o zakończeniu okupowania miejsca. Ponadto, wysyła dane dotyczące zebranego złota i czasu rozpoczęcia oraz zakończenia zbierania. Jeżeli bot, wciąż ma miejsce w plecaku to poszukuje kolejnego miejsca. Natomiast jeżeli plecak jest pełen to musi udać się do miasta w celu jego opróżnienia. Bot działa tak długo, aż nie upłynie wyznaczony limit czasu lub skończy się złoto we wszystkich miejscach dostępnych na mapie.

##### Testy

Do przeprowadzenia i zautomatyzowania testów utworzone zostały odpowiednie skrypty napisane w języku Python. Cassandra została postawiona na kontenerach dockerowych, gdzie uruchomione zostały 4 nody, w tym 2 seedy, które mogły się komunikować przez dodatkowy kontener działający jako router pomiędzy nimi. Wyłączenie interfejsu na routerze pozwalało zasymulować wystąpienie partycji nodów bazy danych. Każdy test trwał 3 minuty i testowane były wyniki dla zmiennej liczby botów oraz czasu oczekiwania, w przypadku wystąpienia i braku partycji.

Poniżej zamieszczony jest wykres przedstawiający uzyskane wyniki:
![results](fig1.png?raw=true "Wyniki przeprowadzonych testów")
