# Karta pracy — SQL od podstaw (PostgreSQL 16)

## Jak korzystać z tej karty pracy

Każda sekcja jest zbudowana tak samo — **najpierw teoria, potem praktyka**:

* **Cel** — co umiesz po przerobieniu sekcji.
* **Teoria** — porcja wiedzy do przeczytania **zanim** zaczniesz pisać zapytania: po co to jest, jak działa, jakie są pułapki. To nie jest opcjonalne — przykłady praktyczne zakładają, że teorię już znasz.
* **Schemat SQL** — `CREATE TABLE` do skopiowania.
* **Dane testowe** — `INSERT`-y żeby było co pytać.
* **Zadania do wykonania** — konkretne zapytania SQL do napisania (minimum 5–8 na zadanie).
* **Pytania kontrolne** — sprawdź zrozumienie.

Na samym końcu (**sekcja 11**) znajdziesz **samodzielne zadania** — bez gotowych zapytań, za to z oczekiwanymi wynikami, żebyś mógł sprawdzić, czy napisałeś dobrze.

**Plik teorii rozszerzonej:** `kurs_zajecia/36_sql.md`. Ta karta jest samowystarczalna — teoria potrzebna do ćwiczeń jest tutaj, w każdej sekcji.

**Kolejność nauki (ważne):** sekcje są ułożone tak, by każda korzystała z wiedzy poprzedniej. Najpierw tworzymy tabele (DDL), zaraz potem uczymy się **pilnować poprawności danych** (constraints), dopiero wtedy zaczynamy wkładać i czytać dane. Nie przeskakuj sekcji.

**Środowisko:** PostgreSQL 16 w Dockerze (sekcja 0 niżej). Wszystkie zapytania w tej karcie są wykonywalne w PostgreSQL 16.

**Konwencje:**

* Tabele i kolumny — `snake_case`.
* Słowa kluczowe SQL — `UPPERCASE` (czytelność).
* Każde zapytanie kończy się `;`.
* Każda tabela ma `PRIMARY KEY`.
* Klucze obce indeksujemy ręcznie (PostgreSQL nie robi tego automatycznie).
* Dla pieniędzy — `NUMERIC(12,2)`. Nigdy `FLOAT` / `DOUBLE PRECISION`.

---

## 0. Środowisko — uruchomienie PostgreSQL w Dockerze

**Cel:** Postawić własny serwer PostgreSQL 16 lokalnie, podłączyć się do niego przez `psql`, DBeaver lub IntelliJ Database, sprawdzić wersję i mieć trwały wolumen na dane.

**Teoria w pigułce:** Docker to lekka wirtualizacja kontenerowa. Zamiast instalować PostgreSQL ręcznie na każdym laptopie kursu (różne wersje, różne ścieżki, awarie pakietów), używamy **gotowego obrazu** `postgres:16` z Docker Huba. Jednym poleceniem startujemy bazę identyczną u każdego kursanta. Wolumen Dockera (`-v kurs_pg_data:/var/lib/postgresql/data`) zapewnia, że dane przeżyją restart kontenera — bez wolumenu wszystko zniknie po `docker rm`.

### 0.1 Wymagania wstępne

Zanim zaczniesz, zainstaluj:

* **Docker Desktop** (macOS/Windows) — pobierz z https://www.docker.com/products/docker-desktop/
* lub **Docker Engine** (Linux) — `sudo apt install docker.io` (Ubuntu/Debian) albo `sudo dnf install docker-ce` (Fedora/RHEL).

Sprawdź, czy Docker działa:

```bash
docker --version
# Docker version 24.x.x, build ...

docker info
# powinno pokazać status "Server: Docker Engine ..." bez błędu
```

Jeśli `docker info` zwraca błąd `Cannot connect to the Docker daemon`, uruchom Docker Desktop (macOS/Windows) lub `sudo systemctl start docker` (Linux).

### 0.2 Pobranie obrazu PostgreSQL 16

```bash
docker pull postgres:16
```

Pierwsze pobranie zajmie ~150 MB, potem już jest cache lokalnie. Sprawdź:

```bash
docker images | grep postgres
# postgres   16   <hash>   2 weeks ago   438MB
```

### 0.3 Uruchomienie kontenera z bazą

Najważniejsze polecenie w tej karcie pracy — startuje kontener z PostgreSQL 16:

```bash
docker run -d \
  --name kurs-postgres \
  -e POSTGRES_PASSWORD=kurs123 \
  -e POSTGRES_USER=kurs \
  -e POSTGRES_DB=kursdb \
  -p 5432:5432 \
  -v kurs_pg_data:/var/lib/postgresql/data \
  postgres:16
```

Co znaczą poszczególne flagi:

| Flaga | Co robi |
|-------|---------|
| `-d` | tryb detached — kontener w tle |
| `--name kurs-postgres` | nazwa kontenera (łatwo go potem zatrzymać/uruchomić) |
| `-e POSTGRES_PASSWORD=kurs123` | hasło administratora bazy |
| `-e POSTGRES_USER=kurs` | login użytkownika (właściciel bazy `kursdb`) |
| `-e POSTGRES_DB=kursdb` | nazwa pierwszej bazy danych utworzonej przy starcie |
| `-p 5432:5432` | mapowanie portu hosta:kontenera — z poziomu hosta łączysz się na `localhost:5432` |
| `-v kurs_pg_data:/var/lib/postgresql/data` | wolumen Docker `kurs_pg_data` montowany w katalogu danych Postgresa — dane przeżyją restart |
| `postgres:16` | obraz i tag |

Sprawdź, czy kontener wstał:

```bash
docker ps
# CONTAINER ID   IMAGE         COMMAND                  CREATED         STATUS         PORTS                    NAMES
# abc1234        postgres:16   "docker-entrypoint.s…"   5 seconds ago   Up 4 seconds   0.0.0.0:5432->5432/tcp   kurs-postgres
```

Jeśli widzisz `Up X seconds` — gotowe. Jeśli kontener zniknął z listy (sprawdź też `docker ps -a`), zobacz logi:

```bash
docker logs kurs-postgres
```

Najczęstszy błąd przy starcie: **`bind for 0.0.0.0:5432 failed: port is already allocated`**. To znaczy, że masz już PostgreSQL na hoście (zainstalowany lokalnie albo inny kontener) zajmujący port 5432. Zatrzymaj go lub uruchom nasz kontener na innym porcie, np. `-p 5433:5432` — i wtedy z hosta łączysz się na `localhost:5433`.

### 0.4 Podłączenie się do bazy — `psql` w kontenerze

Najszybszy sposób — wejść do kontenera i uruchomić wbudowanego klienta `psql`:

```bash
docker exec -it kurs-postgres psql -U kurs -d kursdb
```

Co tu się dzieje:

* `docker exec -it` — wykonaj komendę w działającym kontenerze, interaktywnie (TTY).
* `kurs-postgres` — nazwa kontenera.
* `psql -U kurs -d kursdb` — klient `psql` (PostgreSQL Interactive Terminal) jako użytkownik `kurs`, połącz się z bazą `kursdb`.

Po wejściu zobaczysz znak zachęty:

```
psql (16.x)
Type "help" for help.

kursdb=>
```

Sprawdź wersję serwera — to nasz pierwszy SQL:

```sql
SELECT version();
```

Wynik mniej więcej:

```
PostgreSQL 16.4 (Debian 16.4-1.pgdg120+1) on x86_64-pc-linux-gnu, ...
```

Sprawdź listę baz:

```sql
\l
```

Sprawdź aktualnego użytkownika i bazę:

```sql
SELECT current_user, current_database();
```

Wyjście z `psql`:

```sql
\q
```

### 0.5 Najważniejsze polecenia `psql` (meta-komendy)

`psql` ma własne polecenia zaczynające się od `\` (backslash). Najczęściej używane:

| Polecenie | Co robi |
|-----------|---------|
| `\l` | Lista baz danych |
| `\c kursdb` | Przełącz się na bazę `kursdb` |
| `\dt` | Lista tabel w bieżącej bazie |
| `\d users` | Opis struktury tabeli `users` (kolumny, typy, indeksy) |
| `\d+ users` | Jak wyżej, ale z dodatkowymi informacjami (rozmiar, opis) |
| `\di` | Lista indeksów |
| `\dv` | Lista widoków |
| `\df` | Lista funkcji użytkownika |
| `\du` | Lista ról / użytkowników bazy |
| `\timing` | Włącz/wyłącz mierzenie czasu zapytań |
| `\x` | Tryb wertykalny wyświetlania (przydatne przy długich wierszach) |
| `\e` | Otwórz edytor (`$EDITOR`) z ostatnim zapytaniem |
| `\i plik.sql` | Wykonaj polecenia z pliku |
| `\q` | Wyjdź z psql |
| `\?` | Pełna lista meta-komend |

### 0.6 Alternatywa — DBeaver (klient graficzny)

DBeaver to darmowy klient SQL z GUI, dostępny dla Win/macOS/Linux. Polecany na zajęciach dla osób które wolą widzieć tabelę zamiast tekstu.

**Krok po kroku:**

1. Pobierz z https://dbeaver.io/download/ → Community Edition.
2. Uruchom DBeaver → menu *Database → New Database Connection*.
3. Wybierz **PostgreSQL**.
4. Wpisz parametry:
    * **Host:** `localhost`
    * **Port:** `5432`
    * **Database:** `kursdb`
    * **Username:** `kurs`
    * **Password:** `kurs123`
5. Kliknij *Test Connection*. Jeśli wyskoczy "Driver not found" — DBeaver sam zaproponuje pobranie sterownika JDBC PostgreSQL. Kliknij *Download*.
6. Po sukcesie — *Finish*. Lewa panel: drzewo bazy, prawa: edytor SQL (`Ctrl+Enter` wykonuje zapytanie).

### 0.7 Alternatywa — IntelliJ IDEA Database / DataGrip

Jeśli używasz IntelliJ Ultimate albo DataGrip:

1. *View → Tool Windows → Database* (prawy panel).
2. Plus → *Data Source → PostgreSQL*.
3. Te same parametry co wyżej. IntelliJ sam pobierze sterownik.
4. *Test Connection* → *OK*.
5. Konsola SQL: prawy klik na bazie → *New → Query Console*.

### 0.8 Zarządzanie kontenerem — start, stop, logi

Kontener można zatrzymać i uruchomić bez utraty danych (dzięki wolumenowi):

```bash
# Zatrzymaj (dane pozostają)
docker stop kurs-postgres

# Uruchom ponownie
docker start kurs-postgres

# Restart (stop + start)
docker restart kurs-postgres

# Zobacz logi serwera (przydatne przy debugowaniu)
docker logs kurs-postgres

# Zobacz logi na żywo (Ctrl+C aby wyjść)
docker logs -f kurs-postgres

# Statystyki użycia CPU/RAM
docker stats kurs-postgres
```

### 0.9 Wyczyszczenie i rozpoczęcie od nowa

Jeśli chcesz **całkowicie** wyrzucić bazę i zacząć od zera (np. zaorałeś dane na ćwiczeniach):

```bash
# Zatrzymaj i usuń kontener
docker rm -f kurs-postgres

# Usuń wolumen z danymi
docker volume rm kurs_pg_data

# Sprawdź, że wolumen zniknął
docker volume ls | grep kurs_pg_data
# powinno nic nie zwrócić
```

Potem ponownie odpal komendę z sekcji 0.3 — utworzy nowy, pusty kontener.

### 0.10 Tryb tylko do nauki — bez wolumenu

Jeśli chcesz **świadomie tracić dane** po `docker stop` (np. żeby ćwiczyć od czystego stanu na każdej lekcji), usuń `-v kurs_pg_data:/var/lib/postgresql/data`:

```bash
docker run -d --rm \
  --name kurs-postgres-tmp \
  -e POSTGRES_PASSWORD=kurs123 \
  -e POSTGRES_USER=kurs \
  -e POSTGRES_DB=kursdb \
  -p 5432:5432 \
  postgres:16
```

Flaga `--rm` powoduje automatyczne usunięcie kontenera po `docker stop`.

### 0.11 Pierwsze zapytania weryfikacyjne

Po podłączeniu się przez `psql` lub DBeaver wpisz po kolei:

```sql
SELECT version();

SELECT current_database();

SELECT current_user;

SHOW server_encoding;
-- oczekiwane: UTF8

SHOW timezone;
-- oczekiwane: Etc/UTC (lub podobne)

SELECT NOW();
-- aktualna data i czas serwera bazy
```

Jeżeli wszystkie 6 zapytań zwróciło wynik bez błędu — środowisko działa, możesz przejść do sekcji 1.

### Pytania kontrolne (sekcja 0)

1. Po co używamy flagi `-v kurs_pg_data:/var/lib/postgresql/data` w `docker run`? Co zniknie, jeśli ją pominiemy?
2. Czemu mapujemy port `-p 5432:5432`? Co znaczą obie liczby?
3. Jak sprawdzisz logi działającego kontenera?
4. Polecenie `\dt` w `psql` — co zwraca? A `\d users`?
5. Jak całkowicie usunąć bazę razem z danymi i zacząć od zera?

---

## 1. DDL — typy kolumn, klucze podstawowe i obce

**Cel:** Zaprojektować tabele z odpowiednimi typami danych, kluczem podstawowym i kluczem obcym. Poznać `CREATE TABLE`, `ALTER TABLE`, `DROP TABLE`.

**Teoria w pigułce:** Każda tabela ma `PRIMARY KEY` (unikalny identyfikator wiersza). Kolumny mają **typy** — `INTEGER`, `BIGINT`, `VARCHAR(n)`, `TEXT`, `DATE`, `TIMESTAMP`, `BOOLEAN`, `NUMERIC(p,s)`. Auto-inkrementację w PostgreSQL realizujemy przez **`GENERATED ALWAYS AS IDENTITY`** (standard SQL od PostgreSQL 10) lub starsze **`SERIAL`**. `FOREIGN KEY` referuje klucz główny innej tabeli. `ALTER TABLE` modyfikuje strukturę istniejącej tabeli. `DROP TABLE` usuwa tabelę razem z danymi.

**Teoria szerzej — przeczytaj zanim zaczniesz pisać:**

* **DDL vs DML.** SQL dzieli się na podjęzyki. **DDL** (Data Definition Language: `CREATE`, `ALTER`, `DROP`) opisuje *strukturę* — jak wyglądają tabele. **DML** (Data Manipulation Language: `INSERT`, `UPDATE`, `DELETE`, `SELECT`) operuje na *danych w środku*. W tej sekcji robimy DDL — budujemy „szufladki”, do których w sekcji 3 będziemy wkładać dane.

* **Po co w ogóle typy?** Typ kolumny to obietnica i jednocześnie strażnik. Mówiąc `price NUMERIC(10,2)` deklarujesz, że w tej kolumnie **zawsze** będzie liczba z dwoma miejscami po przecinku — baza odrzuci próbę wpisania tekstu „dużo”. Dobrze dobrany typ to pierwsza warstwa ochrony danych: oszczędza miejsce, przyspiesza zapytania i wyłapuje błędy już przy zapisie.

* **Dlaczego `NUMERIC`, a nie `REAL`/`FLOAT` na pieniądze?** Typy zmiennoprzecinkowe (`REAL`, `DOUBLE PRECISION`) przechowują liczby w postaci binarnej i **nie potrafią dokładnie** zapisać np. `0.10`. Po wielu operacjach `0.1 + 0.2` potrafi dać `0.30000000000000004`. Przy pieniądzach to katastrofa księgowa. `NUMERIC(p,s)` trzyma liczby dokładnie (`p` = łączna liczba cyfr, `s` = cyfry po przecinku), kosztem nieco wolniejszych obliczeń. Reguła: **pieniądze i wszystko, co musi się zgadzać co do grosza → `NUMERIC`.**

* **Klucz główny (`PRIMARY KEY`).** To kolumna (lub zestaw kolumn), która jednoznacznie wskazuje wiersz. Nie może być `NULL` i musi być unikalny. Najczęściej to sztuczny, generowany numer (`id`) — tzw. *klucz syntetyczny*. Dlaczego nie używać np. emaila jako klucza? Bo dane biznesowe się zmieniają (ktoś zmienia email), a klucz powinien być wieczny i niezmienny.

* **Klucz obcy (`FOREIGN KEY`).** To „wskaźnik” z jednej tabeli na klucz główny innej. Mówi: „wartość w `books.publisher_id` musi odpowiadać istniejącemu `publishers.id`”. Dzięki temu baza **nie pozwoli** dodać książki nieistniejącego wydawcy ani (zależnie od `ON DELETE`) usunąć wydawcy, do którego przypięte są książki. To fundament *integralności referencyjnej* — temat sekcji 2.

* **Tabela pośrednicząca (relacja wiele-do-wielu).** Jedna książka może mieć wielu autorów, a jeden autor wiele książek. Relacji „wiele-do-wielu” nie da się zapisać dwoma tabelami — potrzeba trzeciej, łączącej (`book_authors`), z kluczem głównym złożonym z dwóch kluczy obcych `(book_id, author_id)`. To wzorzec, który zobaczysz w każdej bazie.

* **`GENERATED ALWAYS AS IDENTITY` vs `SERIAL`.** Oba dają auto-numerację. `SERIAL` to stary mechanizm (tworzy ukrytą sekwencję, kolumnę można nadpisać ręcznie — łatwo o bałagan). `GENERATED ALWAYS AS IDENTITY` to nowy standard SQL: czytelniejszy i domyślnie **blokuje** ręczne wstawianie `id`. W nowych projektach używaj `IDENTITY`.

* **`NULL` to nie zero i nie pusty string.** `NULL` znaczy „wartość nieznana / brak wartości”. To stan, nie wartość — i zachowuje się inaczej w porównaniach (więcej w sekcji 4). Kolumna `NOT NULL` zabrania braku wartości.

Dopiero z tą wiedzą poniższe `CREATE TABLE` przestaną być „magicznymi zaklęciami” — każde słowo kluczowe ma swój powód.

### 1.1 Najważniejsze typy w PostgreSQL 16

| Typ | Do czego | Przykład |
|-----|----------|----------|
| `SMALLINT` | 2-bajtowy int (-32 768 … 32 767) | `age SMALLINT` |
| `INTEGER` (`INT`) | 4-bajtowy int (-2 mld … 2 mld) | `quantity INT` |
| `BIGINT` | 8-bajtowy int | `user_id BIGINT` |
| `SERIAL` | auto-increment INT (legacy) | `id SERIAL PRIMARY KEY` |
| `BIGSERIAL` | auto-increment BIGINT (legacy) | `id BIGSERIAL PRIMARY KEY` |
| `GENERATED AS IDENTITY` | auto-increment standard SQL | `id BIGINT GENERATED ALWAYS AS IDENTITY` |
| `NUMERIC(p,s)` | dokładna liczba dziesiętna (pieniądze!) | `price NUMERIC(12,2)` |
| `REAL` / `DOUBLE PRECISION` | liczba zmiennoprzecinkowa (NIE dla pieniędzy) | `temperature REAL` |
| `VARCHAR(n)` | tekst do n znaków | `email VARCHAR(150)` |
| `TEXT` | tekst dowolnej długości | `description TEXT` |
| `CHAR(n)` | tekst o stałej długości (rzadko używany) | `code CHAR(3)` |
| `BOOLEAN` | `TRUE`/`FALSE`/`NULL` | `active BOOLEAN` |
| `DATE` | data bez godziny | `birth_date DATE` |
| `TIME` | godzina bez daty | `opens_at TIME` |
| `TIMESTAMP` | data + godzina (bez strefy) | `created_at TIMESTAMP` |
| `TIMESTAMPTZ` | data + godzina ze strefą czasową | `created_at TIMESTAMPTZ` |
| `INTERVAL` | różnica czasowa | `duration INTERVAL` |
| `UUID` | identyfikator 128-bitowy | `id UUID` |
| `JSONB` | JSON binarny (indeksowalny) | `metadata JSONB` |
| `BYTEA` | tablica bajtów (obrazy, pliki) | `avatar BYTEA` |
| `ARRAY` | tablica wartości | `tags TEXT[]` |

### Schemat SQL — sklep książkowy

```sql
DROP TABLE IF EXISTS book_authors CASCADE;
DROP TABLE IF EXISTS books CASCADE;
DROP TABLE IF EXISTS authors CASCADE;
DROP TABLE IF EXISTS publishers CASCADE;

CREATE TABLE publishers (
    id           BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(150) NOT NULL UNIQUE,
    country      VARCHAR(60)  NOT NULL,
    founded_year INTEGER      CHECK (founded_year BETWEEN 1400 AND 2100),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE authors (
    id          BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name  VARCHAR(80)  NOT NULL,
    last_name   VARCHAR(80)  NOT NULL,
    birth_date  DATE,
    nationality VARCHAR(60),
    is_alive    BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE books (
    id            BIGINT         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title         VARCHAR(200)   NOT NULL,
    isbn          VARCHAR(20)    NOT NULL UNIQUE,
    publisher_id  BIGINT         NOT NULL REFERENCES publishers(id) ON DELETE RESTRICT,
    publish_year  INTEGER        NOT NULL CHECK (publish_year BETWEEN 1400 AND 2100),
    price         NUMERIC(10,2)  NOT NULL CHECK (price >= 0),
    pages         INTEGER        CHECK (pages > 0),
    description   TEXT,
    in_stock      INTEGER        NOT NULL DEFAULT 0 CHECK (in_stock >= 0),
    created_at    TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE book_authors (
    book_id   BIGINT NOT NULL REFERENCES books(id)   ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES authors(id) ON DELETE RESTRICT,
    PRIMARY KEY (book_id, author_id)
);
```

### Dane testowe

```sql
INSERT INTO publishers (name, country, founded_year) VALUES
    ('Helion',            'Polska', 1991),
    ('PWN',               'Polska', 1951),
    ('O''Reilly',         'USA',    1978),
    ('Manning',           'USA',    1990),
    ('Pearson',           'USA',    1998);

INSERT INTO authors (first_name, last_name, birth_date, nationality, is_alive) VALUES
    ('Joshua',  'Bloch',        '1961-08-28', 'USA',    TRUE),
    ('Robert',  'Martin',       '1952-12-05', 'USA',    TRUE),
    ('Andrew',  'Hunt',         '1964-01-01', 'USA',    TRUE),
    ('David',   'Thomas',       '1956-01-01', 'USA',    TRUE),
    ('Andrzej', 'Sapkowski',    '1948-06-21', 'Polska', TRUE),
    ('Stanisław','Lem',         '1921-09-13', 'Polska', FALSE),
    ('Martin',  'Fowler',       '1963-12-18', 'UK',     TRUE);

INSERT INTO books (title, isbn, publisher_id, publish_year, price, pages, in_stock) VALUES
    ('Effective Java',                  '978-0134685991', 5, 2018, 199.00, 412, 12),
    ('Clean Code',                      '978-0132350884', 5, 2008, 149.00, 464, 25),
    ('The Pragmatic Programmer',        '978-0135957059', 5, 2019, 169.00, 352, 18),
    ('Refactoring',                     '978-0134757599', 5, 2018, 189.00, 448, 10),
    ('Wiedźmin: Ostatnie życzenie',    '978-8375780635', 1, 1993,  39.90, 320, 50),
    ('Solaris',                         '978-8308045008', 2, 1961,  29.00, 240, 30),
    ('Java in Action',                  '978-1617297588', 4, 2020, 220.00, 580,  5),
    ('PostgreSQL Cookbook',             '978-1492048039', 3, 2021, 175.00, 600,  8);

INSERT INTO book_authors (book_id, author_id) VALUES
    (1, 1),         -- Effective Java — Bloch
    (2, 2),         -- Clean Code — Martin
    (3, 3), (3, 4), -- Pragmatic Programmer — Hunt + Thomas
    (4, 7),         -- Refactoring — Fowler
    (5, 5),         -- Wiedźmin — Sapkowski
    (6, 6),         -- Solaris — Lem
    (7, 1);         -- Java in Action — Bloch
```

### Zadania do wykonania

1. **Stwórz** wszystkie cztery tabele (skopiuj DDL).
2. **Załaduj** dane testowe.
3. **Wyświetl** strukturę tabeli `books`: w psql wpisz `\d books`. Zinterpretuj wynik — które kolumny są `NOT NULL`, gdzie są indeksy, jakie są constraints?
4. **Dodaj** nową kolumnę `language VARCHAR(5)` do tabeli `books` z domyślną wartością `'PL'`:

   ```sql
   ALTER TABLE books ADD COLUMN language VARCHAR(5) NOT NULL DEFAULT 'PL';
   ```

5. **Zmień** typ kolumny `pages` z `INTEGER` na `SMALLINT` (uwaga — to się uda tylko jeśli wszystkie wartości mieszczą się w zakresie SMALLINT):

   ```sql
   ALTER TABLE books ALTER COLUMN pages TYPE SMALLINT;
   ```

6. **Dodaj** ograniczenie sprawdzające, że `language` ma dokładnie 2 znaki (`CHECK (LENGTH(language) = 2)`).
7. **Usuń** kolumnę `description` z tabeli `books`. Potem ją z powrotem dodaj jako `TEXT`.
8. **Spróbuj** usunąć tabelę `publishers`. Co się stanie? Czemu? Spróbuj teraz `DROP TABLE publishers CASCADE` — co to robi?
9. **Stwórz** nową tabelę `book_tags`:
    * `id` — auto-increment PK,
    * `book_id` — FK do `books` z `ON DELETE CASCADE`,
    * `tag` — `VARCHAR(40)`, nie NULL,
    * `UNIQUE (book_id, tag)` — ten sam tag nie może być przypisany dwa razy do tej samej książki.
10. **Stwórz** kopię struktury tabeli `books` jako `books_archive` (bez danych):

    ```sql
    CREATE TABLE books_archive (LIKE books INCLUDING ALL);
    ```

### Pytania kontrolne

1. Czym `GENERATED ALWAYS AS IDENTITY` różni się od `SERIAL`?
2. Czemu na pieniądze używamy `NUMERIC(12,2)`, a nie `REAL`?
3. Co robi `ON DELETE CASCADE` w `book_authors`? Co by się stało gdyby było `RESTRICT`?
4. Czemu klucz główny w tabeli pośredniczącej `book_authors` to **para kolumn** `(book_id, author_id)`?
5. Co robi `\d books` w psql?
6. Jak dodać nową kolumnę z wartością domyślną do istniejącej tabeli?
7. Czym różni się `VARCHAR(200)` od `TEXT`?
8. Co zrobi `DROP TABLE books;` jeśli istnieją wiersze w `book_authors` referujące do `books`?

---

## 2. Constraints — `NOT NULL`, `UNIQUE`, `CHECK`, `FOREIGN KEY` z `ON DELETE`

**Cel:** Dodawać ograniczenia (constraints) do tabel — gwarantują integralność danych. Wybierać właściwą strategię `ON DELETE` / `ON UPDATE`.

**Teoria w pigułce:** Constraints to **reguły walidacji** wymuszane przez bazę. Próba złamania reguły zwraca błąd zamiast zapisać niepoprawne dane.

* **`NOT NULL`** — wartość musi być podana.
* **`UNIQUE`** — wartość (lub kombinacja kolumn) unikalna w całej tabeli.
* **`PRIMARY KEY`** — `NOT NULL` + `UNIQUE` + automatyczny indeks.
* **`CHECK (warunek)`** — dowolny warunek logiczny na wierszu.
* **`FOREIGN KEY (kol) REFERENCES inna(id)`** — wartość musi istnieć w innej tabeli.
* **`ON DELETE`** — co zrobić gdy rodzic jest usunięty:
    * `RESTRICT` / `NO ACTION` — zabroń (default).
    * `CASCADE` — usuń też dziecko.
    * `SET NULL` — wyzeruj FK u dziecka.
    * `SET DEFAULT` — ustaw FK na wartość DEFAULT.
* **`ON UPDATE`** — analogicznie dla zmiany klucza głównego rodzica.

**Teoria szerzej — przeczytaj zanim zaczniesz pisać:**

* **Dlaczego walidacja należy do bazy, a nie tylko do aplikacji?** Można by sprawdzać poprawność w kodzie Javy/Pythona przed zapisem. Problem: do tej samej bazy puka często **wiele** aplikacji, skryptów, importów i ręcznych poprawek przez `psql`. Jeśli reguła („cena nie może być ujemna”) żyje tylko w jednej aplikacji, każda inna droga ją obejdzie. Constraint w bazie to **ostatnia, nieprzekraczalna linia obrony** — pilnuje danych niezależnie od tego, kto i jak je wprowadza.

* **Integralność danych — 3 poziomy.** (1) *Encji* — każdy wiersz jest jednoznaczny (`PRIMARY KEY`). (2) *Domenowa* — wartość mieści się w dozwolonym zakresie (`CHECK`, `NOT NULL`, typ kolumny). (3) *Referencyjna* — relacje między tabelami się zgadzają (`FOREIGN KEY`). Constraints to narzędzia do wymuszania wszystkich trzech.

* **`CHECK` to mini-reguła biznesowa.** W warunku `CHECK` możesz użyć dowolnego wyrażenia logicznego na kolumnach tego samego wiersza: `CHECK (price >= 0)`, `CHECK (check_out > check_in)`, `CHECK (status IN ('NEW','PAID'))`. To świetny sposób na zakodowanie reguł typu „data wymeldowania musi być po zameldowaniu” raz, na zawsze i dla wszystkich.

* **`UNIQUE` pojedyncze vs złożone.** `UNIQUE (email)` — żadne dwa wiersze nie mają tego samego emaila. `UNIQUE (name, city)` — żadne dwa wiersze nie mają tej samej *pary* (ten sam hotel może być w różnych miastach, ale nie dwa razy w tym samym mieście). To różnica, którą łatwo pomylić — `UNIQUE (a, b)` to NIE to samo co dwa osobne `UNIQUE (a)` i `UNIQUE (b)`.

* **`ON DELETE` — to decyzja projektowa, nie techniczna.** Gdy ktoś usuwa rodzica, baza musi wiedzieć, co zrobić z dziećmi. To zależy od *sensu biznesowego*:
    * `RESTRICT`/`NO ACTION` (domyślne) — „nie pozwól usunąć rodzica, dopóki ma dzieci”. Bezpieczne dla danych, których nie chcesz stracić (np. nie kasuj wydawcy, który ma książki).
    * `CASCADE` — „usuń rodzica i pociągnij za sobą dzieci”. Pasuje, gdy dziecko bez rodzica nie ma sensu (pokój bez hotelu, pozycje zamówienia bez zamówienia).
    * `SET NULL` — „usuń rodzica, a u dziecka wyzeruj wskaźnik”. Pasuje, gdy relacja jest opcjonalna (pracownik może zostać bez działu).
      Zła decyzja tutaj to albo utrata danych (`CASCADE` tam, gdzie nie trzeba), albo niemożność niczego usunąć (`RESTRICT` wszędzie).

* **Constraint nazwany vs anonimowy.** `CONSTRAINT fk_rooms_hotel FOREIGN KEY ...` ma czytelną nazwę, która pojawi się w komunikacie błędu i ułatwi późniejsze `ALTER TABLE ... DROP CONSTRAINT`. Bez nazwy PostgreSQL nada własną (np. `rooms_hotel_id_fkey`) — działa, ale gorzej się tym zarządza. W poważnych projektach nazywaj constrainty.

* **Łączymy z DDL z sekcji 1.** Tam już *widziałeś* `NOT NULL`, `CHECK`, `REFERENCES` w `CREATE TABLE`. Tutaj rozumiesz, **dlaczego** tam były i jak je dokładać, zmieniać oraz usuwać już po utworzeniu tabeli (`ALTER TABLE ... ADD/DROP CONSTRAINT`).

### Schemat SQL

```sql
DROP TABLE IF EXISTS reservations CASCADE;
DROP TABLE IF EXISTS rooms CASCADE;
DROP TABLE IF EXISTS hotels CASCADE;

CREATE TABLE hotels (
    id        BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name      VARCHAR(150) NOT NULL,
    city      VARCHAR(60)  NOT NULL,
    stars     SMALLINT     NOT NULL CHECK (stars BETWEEN 1 AND 5),
    email     VARCHAR(120) NOT NULL UNIQUE,
    phone     VARCHAR(20),
    CONSTRAINT unique_hotel_per_city UNIQUE (name, city)
);

CREATE TABLE rooms (
    id           BIGINT         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    hotel_id     BIGINT         NOT NULL,
    room_number  VARCHAR(10)    NOT NULL,
    room_type    VARCHAR(20)    NOT NULL CHECK (room_type IN ('SINGLE', 'DOUBLE', 'SUITE', 'APARTMENT')),
    price_per_night NUMERIC(10,2) NOT NULL CHECK (price_per_night > 0),
    capacity     SMALLINT       NOT NULL CHECK (capacity BETWEEN 1 AND 10),
    is_active    BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_rooms_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
    CONSTRAINT unique_room_per_hotel UNIQUE (hotel_id, room_number)
);

CREATE TABLE reservations (
    id           BIGINT         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    room_id      BIGINT         NOT NULL,
    guest_name   VARCHAR(120)   NOT NULL,
    guest_email  VARCHAR(120)   NOT NULL,
    check_in     DATE           NOT NULL,
    check_out    DATE           NOT NULL,
    total_price  NUMERIC(12,2)  NOT NULL CHECK (total_price >= 0),
    status       VARCHAR(20)    NOT NULL DEFAULT 'CONFIRMED'
                              CHECK (status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED')),
    CONSTRAINT fk_reservations_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT,
    CONSTRAINT check_dates CHECK (check_out > check_in)
);
```

### Dane testowe

```sql
INSERT INTO hotels (name, city, stars, email, phone) VALUES
    ('Hotel Warszawa',  'Warszawa', 4, 'kontakt@hotelwarszawa.pl',  '+48 22 100 200'),
    ('Hotel Kraków',    'Kraków',   5, 'recepcja@hotelkrakow.pl',   '+48 12 100 200'),
    ('Hotel Wrocław',   'Wrocław',  3, 'info@hotelwroclaw.pl',      '+48 71 100 200');

INSERT INTO rooms (hotel_id, room_number, room_type, price_per_night, capacity) VALUES
    (1, '101', 'SINGLE',    250.00, 1),
    (1, '102', 'DOUBLE',    350.00, 2),
    (1, '201', 'SUITE',     650.00, 3),
    (1, '301', 'APARTMENT', 950.00, 4),
    (2, '101', 'SINGLE',    300.00, 1),
    (2, '102', 'DOUBLE',    450.00, 2),
    (2, '501', 'SUITE',     900.00, 3),
    (3, '101', 'SINGLE',    180.00, 1),
    (3, '102', 'DOUBLE',    250.00, 2);

INSERT INTO reservations (room_id, guest_name, guest_email, check_in, check_out, total_price, status) VALUES
    (1, 'Anna Nowak',    'anna@test.pl',    '2024-06-01', '2024-06-05', 1000.00, 'CHECKED_OUT'),
    (3, 'Jan Kowalski',  'jan@test.pl',     '2024-07-10', '2024-07-15', 3250.00, 'CONFIRMED'),
    (5, 'Maria W.',      'maria@test.pl',   '2024-08-01', '2024-08-03',  600.00, 'PENDING'),
    (7, 'Piotr Lis',     'piotr@test.pl',   '2024-09-15', '2024-09-20', 4500.00, 'CONFIRMED'),
    (9, 'Tomasz L.',     'tomek@test.pl',   '2024-10-01', '2024-10-05', 1000.00, 'CONFIRMED');
```

### Zadania do wykonania

1. **`NOT NULL` w akcji** — spróbuj wstawić hotel bez emaila:

   ```sql
   INSERT INTO hotels (name, city, stars) VALUES ('Test', 'Łódź', 3);
   -- Błąd: null value in column "email" violates not-null constraint
   ```

2. **`UNIQUE` w akcji** — dwa hotele z tym samym emailem:

   ```sql
   INSERT INTO hotels (name, city, stars, email) VALUES ('Inny', 'Sopot', 4, 'kontakt@hotelwarszawa.pl');
   -- Błąd: duplicate key value violates unique constraint
   ```

3. **`UNIQUE (a, b)` — `unique_hotel_per_city`**:

   ```sql
   INSERT INTO hotels (name, city, stars, email) VALUES ('Hotel Warszawa', 'Warszawa', 5, 'inny@test.pl');
   -- Błąd: para (name, city) już istnieje
   ```

4. **`CHECK` — dlaczego nie da się wpisać `stars=10`?**

   ```sql
   INSERT INTO hotels (name, city, stars, email) VALUES ('Test', 'Łódź', 10, 'test@test.pl');
   -- Błąd: violates check constraint
   ```

5. **`CHECK` z listą** — `room_type` musi być z listy:

   ```sql
   INSERT INTO rooms (hotel_id, room_number, room_type, price_per_night, capacity)
   VALUES (1, '999', 'PENTHOUSE', 1000, 5);
   -- Błąd
   ```

6. **`CHECK` na relację dat** — próba rezerwacji z `check_out <= check_in`:

   ```sql
   INSERT INTO reservations (room_id, guest_name, guest_email, check_in, check_out, total_price)
   VALUES (1, 'X', 'x@y.pl', '2024-07-10', '2024-07-09', 0);
   -- Błąd: check_dates violation
   ```

7. **`FOREIGN KEY` w akcji** — wstaw pokój dla nieistniejącego hotelu:

   ```sql
   INSERT INTO rooms (hotel_id, room_number, room_type, price_per_night, capacity)
   VALUES (999, '101', 'SINGLE', 200, 1);
   -- Błąd: FK violation
   ```

8. **`ON DELETE CASCADE`** — usuń hotel, zobacz, że pokoje znikają razem z nim. Uwaga: w danych testowych **każdy** hotel ma pokój z rezerwacją, a `reservations` mają wobec `rooms` regułę `ON DELETE RESTRICT` — taka rezerwacja zablokowałaby kaskadę. Dlatego do czystej demonstracji tworzymy hotel testowy bez rezerwacji:

   ```sql
   -- Hotel testowy z jednym pokojem, ale BEZ rezerwacji:
   INSERT INTO hotels (name, city, stars, email)
   VALUES ('Hotel Testowy', 'Gdynia', 3, 'test-cascade@hotel.pl');
   INSERT INTO rooms (hotel_id, room_number, room_type, price_per_night, capacity)
   VALUES ((SELECT id FROM hotels WHERE email = 'test-cascade@hotel.pl'), '101', 'SINGLE', 200, 1);

   -- Usuwamy hotel — pokój zniknie kaskadowo (nic go nie blokuje):
   DELETE FROM hotels WHERE email = 'test-cascade@hotel.pl';  -- działa, bo CASCADE i brak rezerwacji
   -- Pokój '101' tego hotelu został skasowany razem z nim.
   ```

   To jest właśnie różnica między `CASCADE` (pociąga dzieci) a `RESTRICT` (broni przed usunięciem) — zobaczysz ją w następnym zadaniu.

9. **`ON DELETE RESTRICT`** — próba usunięcia pokoju z rezerwacją:

   ```sql
   DELETE FROM rooms WHERE id = 1;
   -- Błąd: still referenced from table "reservations"
   ```

10. **Modyfikacja constraintu — dodaj** `NOT NULL` po fakcie:

    ```sql
    ALTER TABLE hotels ALTER COLUMN phone SET NOT NULL;
    -- Może się nie udać, jeśli są wiersze z NULL — najpierw je uzupełnij
    UPDATE hotels SET phone = '+48 00 000 000' WHERE phone IS NULL;
    ALTER TABLE hotels ALTER COLUMN phone SET NOT NULL;
    ```

11. **`DROP CONSTRAINT`** — usuń constraint:

    ```sql
    ALTER TABLE hotels ALTER COLUMN phone DROP NOT NULL;
    ```

12. **Dodaj nowy CHECK po fakcie**:

    ```sql
    ALTER TABLE rooms ADD CONSTRAINT check_price_max CHECK (price_per_night <= 10000);
    ```

13. **Dodaj nazwany UNIQUE constraint**:

    ```sql
    ALTER TABLE hotels ADD CONSTRAINT unique_phone UNIQUE (phone);
    ```

14. **Lista constraintów na tabeli**:

    ```sql
    SELECT conname, contype, pg_get_constraintdef(c.oid)
    FROM pg_constraint c
    JOIN pg_class t ON c.conrelid = t.oid
    WHERE t.relname = 'hotels';
    ```

15. **`DEFERRABLE`** — odroczone sprawdzanie (zaawansowane):

    ```sql
    CREATE TABLE produkty (
        id INTEGER PRIMARY KEY,
        kod VARCHAR(20) UNIQUE DEFERRABLE INITIALLY DEFERRED
    );
    -- Sprawdzenie unikalności dopiero przy COMMIT, nie po każdym INSERT
    ```

### Pytania kontrolne

1. Czemu `PRIMARY KEY` to `NOT NULL` + `UNIQUE`?
2. Czym `UNIQUE (a, b)` różni się od `UNIQUE (a), UNIQUE (b)`?
3. Co robi `ON DELETE CASCADE`?
4. Co robi `ON DELETE SET NULL`? Kiedy się przyda?
5. Kiedy lepiej `RESTRICT`, a kiedy `CASCADE`?
6. Czy można dodać `NOT NULL` do istniejącej kolumny z NULL-ami?
7. Co robi `CHECK (check_out > check_in)`?

---

## 3. DML — `INSERT`, `UPDATE`, `DELETE`, transakcje

**Cel:** Modyfikować dane: wstawiać pojedyncze i wiele wierszy naraz, aktualizować z `WHERE`, usuwać bezpiecznie, opakowywać operacje w transakcje.

**Teoria w pigułce:** `INSERT INTO tabela (kolumny) VALUES (...)` wstawia wiersze. `UPDATE tabela SET kolumna = ... WHERE ...` zmienia (zawsze z `WHERE`!). `DELETE FROM tabela WHERE ...` usuwa (też zawsze z `WHERE`). `BEGIN; ... COMMIT;` lub `ROLLBACK;` opakowuje grupę operacji w transakcję. Bez `WHERE` w UPDATE/DELETE — modyfikujesz **wszystkie** wiersze. To najczęstsza katastrofalna pomyłka.

**Teoria szerzej — przeczytaj zanim zaczniesz pisać:**

* **`INSERT` — zawsze wymieniaj kolumny.** Forma `INSERT INTO t VALUES (...)` (bez listy kolumn) zależy od kolejności kolumn w tabeli — wystarczy, że ktoś doda kolumnę i wszystkie takie INSERT-y się sypią. Pisz `INSERT INTO t (a, b, c) VALUES (...)`. Wiele wierszy wstawiamy jednym poleceniem, oddzielając krotki przecinkiem — to **znacznie** szybsze niż 1000 osobnych INSERT-ów (jedno połączenie, jedna transakcja, mniej narzutu).

* **`RETURNING` — perełka PostgreSQL.** `INSERT ... RETURNING id` zwraca dane właśnie wstawionego wiersza (np. wygenerowane `id`), bez osobnego `SELECT`. Działa też z `UPDATE` i `DELETE` (`RETURNING *`). Oszczędza zapytanie i eliminuje wyścig (race condition) przy pobieraniu „ostatniego id”.

* **`UPDATE`/`DELETE` zawsze z `WHERE` — i to nie jest przesada.** `UPDATE books SET price = 0;` ustawi cenę **wszystkich** książek na zero. `DELETE FROM book_orders;` skasuje **wszystkie** zamówienia. Bazie jest wszystko jedno — wykona dokładnie to, co napisałeś. Nawyk: najpierw napisz `SELECT ... WHERE ...`, sprawdź, *które* wiersze trafia, dopiero potem zamień `SELECT *` na `UPDATE ... SET` z tym samym `WHERE`.

* **Czym jest transakcja (`BEGIN ... COMMIT`).** To grupa operacji, która wykonuje się „wszystko albo nic”. Klasyczny przykład: przelew — odejmij z konta A, dodaj do konta B. Jeśli między tymi krokami coś padnie, nie chcesz świata, w którym pieniądze zniknęły z A, ale nie dotarły do B. `BEGIN` otwiera transakcję, `COMMIT` ją zatwierdza (zmiany stają się trwałe i widoczne dla innych), `ROLLBACK` wycofuje **wszystko** od `BEGIN`. Dopóki nie ma `COMMIT`, zmiany są „prywatne” — inni ich nie widzą.

* **ACID — dlaczego transakcjom można ufać.** To cztery gwarancje bazy: **A**tomicity (wszystko-albo-nic), **C**onsistency (constrainty są pilnowane), **I**solation (równoległe transakcje sobie nie mieszają), **D**urability (po `COMMIT` dane przeżyją nawet awarię zasilania). Nie musisz tego implementować — dostajesz za darmo, opakowując operacje w `BEGIN/COMMIT`.

* **`SAVEPOINT` — częściowe cofnięcie.** Wewnątrz transakcji można postawić znacznik (`SAVEPOINT s1`) i później wrócić do niego (`ROLLBACK TO SAVEPOINT s1`), nie przerywając całej transakcji. Przydatne przy długich operacjach, gdzie chcesz wycofać tylko ostatni, nieudany krok.

* **Po `COMMIT` nie ma odwrotu.** `ROLLBACK` działa tylko *przed* `COMMIT`. Po zatwierdzeniu jedynym ratunkiem jest backup albo ręczna korekta. Stąd nawyk testowania groźnych `UPDATE`/`DELETE` w transakcji: `BEGIN; ...; SELECT` (sprawdź szkody) i dopiero świadomie `COMMIT` albo `ROLLBACK`.

### Schemat SQL

Używamy schematu z sekcji 1 (publishers, authors, books, book_authors). Dodatkowo:

```sql
DROP TABLE IF EXISTS book_orders CASCADE;

CREATE TABLE book_orders (
    id            BIGINT         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    book_id       BIGINT         NOT NULL REFERENCES books(id) ON DELETE RESTRICT,
    customer_name VARCHAR(120)   NOT NULL,
    quantity      INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price    NUMERIC(10,2)  NOT NULL CHECK (unit_price >= 0),
    status        VARCHAR(20)    NOT NULL DEFAULT 'NEW' CHECK (status IN ('NEW', 'PAID', 'SHIPPED', 'CANCELLED')),
    ordered_at    TIMESTAMP      NOT NULL DEFAULT NOW()
);
```

### Zadania do wykonania

1. **Wstaw** pojedynczego autora "Bjarne Stroustrup", urodzony 1950-12-30, Dania, żyje:

   ```sql
   INSERT INTO authors (first_name, last_name, birth_date, nationality, is_alive)
   VALUES ('Bjarne', 'Stroustrup', '1950-12-30', 'Dania', TRUE);
   ```

2. **Wstaw** trzech autorów jednym `INSERT`:

   ```sql
   INSERT INTO authors (first_name, last_name, nationality, is_alive) VALUES
       ('Linus',  'Torvalds', 'Finlandia', TRUE),
       ('Donald', 'Knuth',    'USA',       TRUE),
       ('Ada',    'Lovelace', 'UK',        FALSE);
   ```

3. **Wstaw** zamówienie z `RETURNING id` (pobierz wygenerowane ID):

   ```sql
   INSERT INTO book_orders (book_id, customer_name, quantity, unit_price)
   VALUES (1, 'Anna Nowak', 2, 199.00)
   RETURNING id, ordered_at;
   ```

4. **Wstaw** zamówienie z polem `status` ustawionym wprost na `'PAID'`. Sprawdź, czy działa. Spróbuj wstawić ze `status = 'UNKNOWN'` — co się stanie?
5. **Wstaw** kilka zamówień na różne książki dla różnych klientów (minimum 8 wierszy) — przyda się w kolejnych zadaniach.

   ```sql
   INSERT INTO book_orders (book_id, customer_name, quantity, unit_price, status) VALUES
       (1, 'Anna Nowak',       1, 199.00, 'PAID'),
       (2, 'Jan Kowalski',     2, 149.00, 'SHIPPED'),
       (3, 'Anna Nowak',       1, 169.00, 'NEW'),
       (5, 'Piotr Lis',        3,  39.90, 'PAID'),
       (5, 'Maria Wiśniewska', 1,  39.90, 'CANCELLED'),
       (6, 'Jan Kowalski',     1,  29.00, 'PAID'),
       (7, 'Anna Nowak',       1, 220.00, 'PAID'),
       (8, 'Piotr Lis',        1, 175.00, 'NEW');
   ```

6. **Zaktualizuj** cenę książki o id=1 na 209.00:

   ```sql
   UPDATE books SET price = 209.00 WHERE id = 1;
   ```

7. **Podnieś** cenę wszystkich książek wydawnictwa Pearson o 10%:

   ```sql
   UPDATE books
   SET price = ROUND(price * 1.10, 2)
   WHERE publisher_id = (SELECT id FROM publishers WHERE name = 'Pearson');
   ```

8. **Zmień** status wszystkich zamówień klienta 'Anna Nowak' z `NEW` na `PAID`:

   ```sql
   UPDATE book_orders SET status = 'PAID'
   WHERE customer_name = 'Anna Nowak' AND status = 'NEW';
   ```

9. **Zmniejsz** `in_stock` o `quantity` dla każdego zamówienia ze statusem `SHIPPED`. (Wskazówka: użyj `UPDATE ... FROM`.) Najpierw zaproponuj zapytanie, potem porównaj:

   ```sql
   UPDATE books
   SET in_stock = in_stock - sub.qty
   FROM (SELECT book_id, SUM(quantity) AS qty
         FROM book_orders
         WHERE status = 'SHIPPED'
         GROUP BY book_id) sub
   WHERE books.id = sub.book_id;
   ```

10. **Usuń** wszystkie zamówienia ze statusem `CANCELLED`:

    ```sql
    DELETE FROM book_orders WHERE status = 'CANCELLED';
    ```

11. **Spróbuj** usunąć wydawnictwo Pearson (`DELETE FROM publishers WHERE name = 'Pearson';`). Co się stanie? Czemu? Jak to naprawić nie tracąc danych historycznych?
12. **Transakcja** — zatwierdź:

    ```sql
    BEGIN;
    INSERT INTO book_orders (book_id, customer_name, quantity, unit_price, status)
        VALUES (2, 'Tomasz Lewandowski', 5, 149.00, 'NEW');
    UPDATE books SET in_stock = in_stock - 5 WHERE id = 2;
    COMMIT;
    ```

13. **Transakcja** — wycofaj:

    ```sql
    BEGIN;
    UPDATE books SET price = 999999;            -- ZGROZA: bez WHERE!
    SELECT id, title, price FROM books LIMIT 5; -- sprawdź szkody
    ROLLBACK;                                   -- wróć do stanu sprzed BEGIN
    SELECT id, title, price FROM books LIMIT 5; -- dane oryginalne
    ```

14. **SAVEPOINT** — częściowy rollback w transakcji:

    ```sql
    BEGIN;
    INSERT INTO book_orders (book_id, customer_name, quantity, unit_price) VALUES (1, 'Test1', 1, 199);
    SAVEPOINT po_pierwszym;
    INSERT INTO book_orders (book_id, customer_name, quantity, unit_price) VALUES (1, 'Test2', 1, 199);
    ROLLBACK TO SAVEPOINT po_pierwszym;
    COMMIT;
    -- Test1 zostanie, Test2 wycofany
    ```

### Pytania kontrolne

1. Co się stanie gdy napiszesz `UPDATE books SET price = 0;` bez `WHERE`?
2. Po co `RETURNING` w `INSERT`?
3. Czemu `INSERT` jednym poleceniem wielu wierszy jest szybszy niż 1000 osobnych `INSERT`-ów?
4. Co robi `BEGIN; ... ROLLBACK;`?
5. Czym `SAVEPOINT` różni się od pełnego `ROLLBACK`?
6. Po `COMMIT` da się jeszcze cofnąć zmiany?
7. Dlaczego nie udało się usunąć wydawnictwa Pearson?
8. Co zrobi `DELETE FROM book_orders;` (bez WHERE) w transakcji bez COMMIT/ROLLBACK?

---

## 4. `SELECT` podstawowy — `WHERE`, `AND/OR`, `BETWEEN`, `IN`, `LIKE`, `IS NULL`

**Cel:** Filtrować wiersze przez różne operatory logiczne i tekstowe.

**Teoria w pigułce:** `WHERE warunek` filtruje wiersze. Operatory: `=`, `<>`, `<`, `>`, `<=`, `>=`. Łączenie warunków: `AND`, `OR`, `NOT`. `BETWEEN x AND y` — zakres (włącznie). `IN (a, b, c)` — wartość z listy. `LIKE 'wzorzec'` — wzorzec ze znakami specjalnymi `%` (dowolny ciąg) i `_` (jeden znak). `ILIKE` — to samo bez wrażliwości na wielkość liter (PostgreSQL specyfik). Porównanie z `NULL` przez `=` jest **zawsze fałszywe** — używaj `IS NULL` / `IS NOT NULL`.

**Teoria szerzej — przeczytaj zanim zaczniesz pisać:**

* **`SELECT` to serce SQL.** Struktura: `SELECT które_kolumny FROM która_tabela WHERE które_wiersze`. `SELECT *` bierze wszystkie kolumny (wygodne przy nauce, ale na produkcji wymieniaj kolumny — szybciej i odporniej na zmiany schematu). `WHERE` decyduje, *które wiersze* przejdą — to filtr nakładany na każdy wiersz osobno.

* **Logika trójwartościowa (`TRUE`/`FALSE`/`UNKNOWN`) — najważniejsza pułapka SQL.** W większości języków warunek jest `true` albo `false`. W SQL dochodzi trzeci stan: `UNKNOWN`, który pojawia się, gdy w grę wchodzi `NULL`. `salary > 10000` dla pracownika z `NULL` w pensji daje `UNKNOWN` — a `WHERE` przepuszcza tylko wiersze z wynikiem `TRUE`. Dlatego `WHERE phone = NULL` **nigdy** nic nie zwróci (porównanie z `NULL` to zawsze `UNKNOWN`, nie `TRUE`). Do `NULL` służą wyłącznie `IS NULL` i `IS NOT NULL`.

* **`AND`/`OR` i nawiasy.** `AND` ma wyższy priorytet niż `OR` (jak `*` przed `+`). Warunek `a OR b AND c` to `a OR (b AND c)`, co rzadko jest tym, o co chodziło. Gdy mieszasz `AND` i `OR`, **zawsze** używaj nawiasów — czytelność i poprawność.

* **`BETWEEN` jest obustronnie domknięty.** `BETWEEN 9000 AND 12000` to `>= 9000 AND <= 12000` — obie granice *włącznie*. Przy datach to częsta pomyłka: `BETWEEN '2024-01-01' AND '2024-01-31'` nie złapie zdarzeń z 31 stycznia po godzinie 00:00, jeśli kolumna to `TIMESTAMP`. Dla przedziałów czasu bezpieczniej: `>= '2024-01-01' AND < '2024-02-01'`.

* **`IN` to skrót dla wielu `OR`.** `department_id IN (1, 3)` to czytelniejsze `department_id = 1 OR department_id = 3`. Uwaga na `NOT IN` z listą zawierającą `NULL` — przez logikę trójwartościową potrafi zwrócić zero wierszy. Bezpieczniejszy bywa `NOT EXISTS` (sekcja 8).

* **`LIKE` — dopasowanie wzorca tekstu.** `%` zastępuje dowolny ciąg (też pusty), `_` dokładnie jeden znak. `'K%'` = zaczyna się na K; `'%ski'` = kończy na „ski”; `'N____'` = N i dokładnie 4 dowolne znaki. `LIKE` rozróżnia wielkość liter; `ILIKE` (specyfik PostgreSQL) — nie. Jeśli szukasz dosłownego `%` lub `_`, trzeba je „uciec” (`ESCAPE`).

* **Alias kolumny (`AS`).** `SELECT salary AS pensja` zmienia tylko nagłówek w wyniku — przydaje się do czytelności i do nazwania kolumn obliczanych (`quantity * unit_price AS wartosc`). Aliasu z `SELECT` nie można (zwykle) użyć w tym samym `WHERE`, bo `WHERE` wykonuje się *przed* `SELECT` (patrz kolejność wykonania w sekcji 6).

### Schemat SQL — pracownicy

```sql
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS departments CASCADE;

CREATE TABLE departments (
    id         BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name       VARCHAR(80)  NOT NULL UNIQUE,
    city       VARCHAR(60)  NOT NULL,
    budget     NUMERIC(12,2) NOT NULL CHECK (budget >= 0)
);

CREATE TABLE employees (
    id            BIGINT         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name    VARCHAR(60)    NOT NULL,
    last_name     VARCHAR(60)    NOT NULL,
    email         VARCHAR(120)   NOT NULL UNIQUE,
    phone         VARCHAR(20),
    department_id BIGINT         REFERENCES departments(id) ON DELETE SET NULL,
    salary        NUMERIC(10,2)  NOT NULL CHECK (salary >= 0),
    hire_date     DATE           NOT NULL,
    manager_id    BIGINT         REFERENCES employees(id) ON DELETE SET NULL,
    is_active     BOOLEAN        NOT NULL DEFAULT TRUE
);
```

### Dane testowe

```sql
INSERT INTO departments (name, city, budget) VALUES
    ('Engineering', 'Warszawa',  1500000.00),
    ('Marketing',   'Kraków',     500000.00),
    ('Sales',       'Warszawa',   800000.00),
    ('HR',          'Wrocław',    250000.00),
    ('Legal',       'Warszawa',   300000.00);

INSERT INTO employees (first_name, last_name, email, phone, department_id, salary, hire_date, manager_id, is_active) VALUES
    ('Anna',     'Nowak',        'anna.nowak@firma.pl',     '+48 600 100 200', 1, 15000.00, '2018-03-15', NULL, TRUE),
    ('Jan',      'Kowalski',     'jan.kowalski@firma.pl',   '+48 600 100 201', 1, 12000.00, '2019-06-01', 1,    TRUE),
    ('Maria',    'Wiśniewska',   'maria.w@firma.pl',        NULL,              1, 11000.00, '2020-01-10', 1,    TRUE),
    ('Piotr',    'Lis',          'piotr.lis@firma.pl',      '+48 600 100 202', 2,  9500.00, '2017-09-20', NULL, TRUE),
    ('Katarzyna','Wójcik',       'k.wojcik@firma.pl',       '+48 600 100 203', 2,  8000.00, '2021-04-12', 4,    TRUE),
    ('Tomasz',   'Lewandowski',  'tomek@firma.pl',          '+48 600 100 204', 3, 13500.00, '2016-11-05', NULL, TRUE),
    ('Marta',    'Zielińska',    'marta.z@firma.pl',        NULL,              3,  9000.00, '2022-02-14', 6,    TRUE),
    ('Krzysztof','Szymański',    'krzysztof.s@firma.pl',    '+48 600 100 205', 4,  7500.00, '2015-08-30', NULL, FALSE),
    ('Magdalena','Kamińska',     'magda.k@firma.pl',        '+48 600 100 206', 5, 18000.00, '2014-05-18', NULL, TRUE),
    ('Paweł',    'Jankowski',    'pawel.j@firma.pl',        NULL,              NULL, 10000.00, '2023-01-09', NULL, TRUE),
    ('Aleksandra','Pawlak',      'ola.p@firma.pl',          '+48 600 100 207', 1, 16500.00, '2017-07-22', NULL, TRUE),
    ('Bartosz',  'Wróbel',       'bartek.w@firma.pl',       '+48 600 100 208', 3,  8500.00, '2023-03-01', 6,    TRUE);
```

### Zadania do wykonania

1. **Wszystkie kolumny** wszystkich pracowników:

   ```sql
   SELECT * FROM employees;
   ```

2. **Konkretne kolumny** — imię, nazwisko, pensja — wszystkich pracowników działu 1:

   ```sql
   SELECT first_name, last_name, salary FROM employees WHERE department_id = 1;
   ```

3. **Pracownicy aktywni z pensją powyżej 10 000**:

   ```sql
   SELECT first_name, last_name, salary FROM employees
   WHERE is_active = TRUE AND salary > 10000;
   ```

4. **Pracownicy z działu 1 LUB 3**:

   ```sql
   SELECT first_name, last_name, department_id FROM employees
   WHERE department_id = 1 OR department_id = 3;
   ```

5. To samo zapytanie napisz krócej używając **`IN`**:

   ```sql
   SELECT first_name, last_name, department_id FROM employees
   WHERE department_id IN (1, 3);
   ```

6. **Pracownicy z pensją w zakresie 9000–12000** — użyj `BETWEEN`:

   ```sql
   SELECT first_name, last_name, salary FROM employees
   WHERE salary BETWEEN 9000 AND 12000;
   ```

7. **Pracownicy zatrudnieni w 2020 lub później**:

   ```sql
   SELECT first_name, last_name, hire_date FROM employees
   WHERE hire_date >= '2020-01-01';
   ```

8. **Pracownicy zatrudnieni między 2017-01-01 a 2019-12-31**:

   ```sql
   SELECT first_name, last_name, hire_date FROM employees
   WHERE hire_date BETWEEN '2017-01-01' AND '2019-12-31';
   ```

9. **Pracownicy bez numeru telefonu** (`IS NULL`):

   ```sql
   SELECT first_name, last_name, phone FROM employees WHERE phone IS NULL;
   ```

10. **Pracownicy z numerem telefonu** (`IS NOT NULL`):

    ```sql
    SELECT first_name, last_name, phone FROM employees WHERE phone IS NOT NULL;
    ```

11. **Pracownicy nieprzypisani do żadnego działu**:

    ```sql
    SELECT first_name, last_name FROM employees WHERE department_id IS NULL;
    ```

12. **`LIKE` — nazwiska zaczynające się na "K"**:

    ```sql
    SELECT first_name, last_name FROM employees WHERE last_name LIKE 'K%';
    ```

13. **`LIKE` — nazwiska kończące się na "ski" lub "ska"**:

    ```sql
    SELECT first_name, last_name FROM employees
    WHERE last_name LIKE '%ski' OR last_name LIKE '%ska';
    ```

14. **`ILIKE` — emaile zawierające "anna" niezależnie od wielkości liter**:

    ```sql
    SELECT email FROM employees WHERE email ILIKE '%anna%';
    ```

15. **`LIKE` z `_`** — nazwiska 5-literowe zaczynające się na "N" (np. `Nowak`):

    ```sql
    SELECT last_name FROM employees WHERE last_name LIKE 'N____';
    ```

16. **Negacja** — pracownicy NIE z Warszawy. Wskazówka: `NOT IN` lub `department_id NOT IN (SELECT id FROM departments WHERE city = 'Warszawa')`:

    ```sql
    SELECT e.first_name, e.last_name
    FROM employees e
    WHERE e.department_id NOT IN (SELECT id FROM departments WHERE city = 'Warszawa');
    ```

17. **Aktywni pracownicy** — dwie wersje:

    ```sql
    SELECT * FROM employees WHERE is_active = TRUE;
    SELECT * FROM employees WHERE is_active;        -- skrót dla BOOLEAN
    ```

18. **Nieaktywni** — `NOT is_active` lub `is_active = FALSE`:

    ```sql
    SELECT * FROM employees WHERE NOT is_active;
    ```

19. **Pracownicy bez managera** — `manager_id IS NULL`. Jakie to role?
20. **Złożony filtr** — aktywni pracownicy z pensją między 8000 a 13000, ze sterowanego działu (1 lub 2), zatrudnieni przed 2022:

    ```sql
    SELECT first_name, last_name, salary, hire_date FROM employees
    WHERE is_active = TRUE
      AND salary BETWEEN 8000 AND 13000
      AND department_id IN (1, 2)
      AND hire_date < '2022-01-01';
    ```

### Pytania kontrolne

1. Czemu `WHERE phone = NULL` jest zawsze fałszywe? Co napisać zamiast tego?
2. Czym `LIKE` różni się od `ILIKE` w PostgreSQL?
3. Co znaczy `%` i `_` w `LIKE`?
4. `BETWEEN 9000 AND 12000` — czy granice są włączone, czy wyłączone?
5. Czemu pisanie `WHERE is_active = TRUE` można skrócić do `WHERE is_active`?
6. Co zwróci `SELECT 5 IN (1, 2, NULL)`? A `SELECT 5 NOT IN (1, 2, NULL)`?
7. Czy `LIKE 'A%'` znajdzie "anna"? A `ILIKE 'A%'`?

---

## 5. Sortowanie i ograniczenia — `ORDER BY`, `LIMIT`, `OFFSET`, `DISTINCT`

**Cel:** Sortować wyniki, ograniczać liczbę zwracanych wierszy, paginować, eliminować duplikaty.

**Teoria w pigułce:** `ORDER BY kol [ASC|DESC]` — sortowanie (ASC domyślnie). `LIMIT n` — maksymalnie n wierszy. `OFFSET m` — pomiń pierwsze m wierszy. Razem: `LIMIT 10 OFFSET 20` — strona 3 po 10. `DISTINCT` przed listą kolumn — unikalne kombinacje. `NULLS FIRST` / `NULLS LAST` — jak sortować NULL-e.

**Teoria szerzej — przeczytaj zanim zaczniesz pisać:**

* **Bez `ORDER BY` kolejność wierszy jest nieokreślona.** Baza może zwrócić wiersze w dowolnej kolejności (zależnie od tego, jak akurat leżą na dysku, czy użyto indeksu itd.). Jeśli zależy Ci na kolejności — *musisz* napisać `ORDER BY`. Nie zakładaj, że „wstawione jako pierwsze = zwrócone jako pierwsze”.

* **`LIMIT` ma sens praktyczny tylko z `ORDER BY`.** „Top 5 zarabiających” to `ORDER BY salary DESC LIMIT 5`. Samo `LIMIT 5` bez sortowania zwróci *jakieś* 5 wierszy — przy każdym uruchomieniu mogą być inne. „Top” bez „wg czego” nie znaczy nic.

* **Paginacja `LIMIT`/`OFFSET`.** Strona `N` o rozmiarze `P`: `LIMIT P OFFSET (N-1)*P`. Strona 1: `OFFSET 0`, strona 2: `OFFSET P` itd. Wada: przy dużych `OFFSET` baza i tak musi przejść i odrzucić wszystkie pominięte wiersze — przy stronie 100 000 to wolne. W praktyce do dużych zbiorów stosuje się paginację „po kluczu” (`WHERE id > ostatnie_id`), ale na nauce `LIMIT/OFFSET` w zupełności wystarczy.

* **Stabilność sortowania.** Gdy sortujesz po kolumnie z powtórzeniami (np. `department_id`), wiersze w obrębie tej samej wartości mają *nieokreśloną* kolejność. Jeśli chcesz powtarzalnego wyniku (ważne przy paginacji!), dodaj drugi klucz rozstrzygający, np. `ORDER BY department_id, id`.

* **`NULL` w sortowaniu.** PostgreSQL domyślnie traktuje `NULL` jako „największe” — przy `ASC` lądują na końcu, przy `DESC` na początku. Możesz to wymusić wprost: `ORDER BY manager_id ASC NULLS LAST`.

* **`DISTINCT` vs `DISTINCT ON`.** `SELECT DISTINCT a, b` usuwa zduplikowane *kombinacje* (a, b). `DISTINCT ON (a)` (specyfik PostgreSQL) zwraca *po jednym* wierszu dla każdej wartości `a` — który dokładnie, decyduje `ORDER BY`. Klasyk: „dla każdego działu pracownik o najwyższej pensji” = `DISTINCT ON (department_id) ... ORDER BY department_id, salary DESC`.

* **`ORDER BY` po numerze i po aliasie.** Można sortować po pozycji kolumny (`ORDER BY 2` = druga kolumna z `SELECT`) lub po aliasie (`ORDER BY years_in_company`). Sortowanie po numerze bywa wygodne, ale kruche — gdy ktoś zmieni listę kolumn, sortowanie „przeskoczy”. Po nazwie/aliasie jest bezpieczniej.

### Schemat SQL

Używamy tabel `employees` i `departments` z sekcji 4.

### Zadania do wykonania

1. **Pracownicy posortowani alfabetycznie po nazwisku**:

   ```sql
   SELECT last_name, first_name FROM employees ORDER BY last_name ASC;
   ```

2. **Najwyżsi zarabiający na górze**:

   ```sql
   SELECT first_name, last_name, salary FROM employees ORDER BY salary DESC;
   ```

3. **Sortowanie po dwóch kolumnach** — dział rosnąco, wewnątrz działu pensja malejąco:

   ```sql
   SELECT first_name, last_name, department_id, salary FROM employees
   ORDER BY department_id ASC, salary DESC;
   ```

4. **Sortowanie z `NULLS LAST`** — najpierw pracownicy z managerem, potem ci bez:

   ```sql
   SELECT first_name, last_name, manager_id FROM employees
   ORDER BY manager_id ASC NULLS LAST;
   ```

5. **Top 5 najwyżej zarabiających**:

   ```sql
   SELECT first_name, last_name, salary FROM employees
   ORDER BY salary DESC LIMIT 5;
   ```

6. **Strona 2 z 5 wynikami na stronę** (czyli wiersze 6-10):

   ```sql
   SELECT first_name, last_name, salary FROM employees
   ORDER BY salary DESC LIMIT 5 OFFSET 5;
   ```

7. **5 najstarszych pracowników firmy** (po `hire_date`):

   ```sql
   SELECT first_name, last_name, hire_date FROM employees
   ORDER BY hire_date ASC LIMIT 5;
   ```

8. **`DISTINCT` — unikalne miasta** w których są działy:

   ```sql
   SELECT DISTINCT city FROM departments;
   ```

9. **`DISTINCT` po kombinacji** — unikalne pary `(city, name)`:

   ```sql
   SELECT DISTINCT city, name FROM departments;
   ```

10. **`SELECT DISTINCT ON` (PostgreSQL specyfik)** — dla każdego działu pracownik z najwyższą pensją:

    ```sql
    SELECT DISTINCT ON (department_id) department_id, first_name, last_name, salary
    FROM employees
    WHERE department_id IS NOT NULL
    ORDER BY department_id, salary DESC;
    ```

11. **Top 3 najwyżej zarabiające osoby w każdym dziale** — to wymaga okien, ale spróbuj prostą wersję dla działu 1 z `LIMIT`:

    ```sql
    SELECT first_name, last_name, salary FROM employees
    WHERE department_id = 1 ORDER BY salary DESC LIMIT 3;
    ```

12. **Sortowanie po wyrażeniu** — pracownicy posortowani po liczbie lat w firmie (od najdłuższych):

    ```sql
    SELECT first_name, last_name, hire_date,
           EXTRACT(YEAR FROM AGE(NOW(), hire_date)) AS years_in_company
    FROM employees
    ORDER BY years_in_company DESC;
    ```

13. **Paginacja stronicowana** — napisz zapytanie zwracające pracowników dla strony `N` z rozmiarem strony `P` (przyjmij N=3, P=4):

    ```sql
    SELECT id, first_name, last_name FROM employees
    ORDER BY id LIMIT 4 OFFSET (3 - 1) * 4;
    ```

14. **`FETCH FIRST n ROWS ONLY`** — standardowy SQL (PostgreSQL też wspiera):

    ```sql
    SELECT first_name, last_name, salary FROM employees
    ORDER BY salary DESC FETCH FIRST 5 ROWS ONLY;
    ```

### Pytania kontrolne

1. Czym `ORDER BY salary` różni się od `ORDER BY salary DESC`?
2. Co zrobi `ORDER BY 2` — sortuje po której kolumnie?
3. Co robi `NULLS LAST`?
4. Jak działają `LIMIT` i `OFFSET` razem przy paginacji?
5. Czym `DISTINCT` różni się od `DISTINCT ON (kolumna)` w PostgreSQL?
6. Czy `LIMIT 5` bez `ORDER BY` zawsze zwraca te same 5 wierszy? Czemu nie?

---

## 6. Agregacje — `COUNT`, `SUM`, `AVG`, `MIN`, `MAX`, `GROUP BY`, `HAVING`

**Cel:** Liczyć, sumować, średniować po grupach.

**Teoria w pigułce:** Funkcje agregujące (`COUNT`, `SUM`, `AVG`, `MIN`, `MAX`) bez `GROUP BY` operują na całej tabeli. Z `GROUP BY kol` — w grupach. Każda nieagregowana kolumna w `SELECT` musi być w `GROUP BY`. `WHERE` filtruje wiersze **przed** agregacją, `HAVING` filtruje grupy **po** agregacji. Kolejność: `FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT`.

**Teoria szerzej — przeczytaj zanim zaczniesz pisać:**

* **Czym jest agregacja.** Dotąd jeden wiersz wejściowy = jeden wiersz wyniku. Agregacja **zwija wiele wierszy w jedną liczbę**: 100 pracowników → jedna średnia pensja. To zmiana sposobu myślenia: nie pytamy „pokaż wiersze”, tylko „policz/podsumuj”.

* **`GROUP BY` dzieli tabelę na kubełki.** `GROUP BY department_id` tworzy po jednej grupie na każdy dział, a funkcja agregująca liczy się *osobno w każdej grupie*. Wynik ma tyle wierszy, ile jest grup. To fundament raportów: „suma sprzedaży per region”, „liczba zamówień per klient”, „średnia ocena per produkt”.

* **Złota zasada `GROUP BY`.** Każda kolumna w `SELECT`, która **nie** jest opakowana w funkcję agregującą, **musi** być wymieniona w `GROUP BY`. Inaczej baza nie wie, którą z wielu wartości w grupie pokazać — i zgłasza błąd. (`SELECT department_id, first_name, COUNT(*) ... GROUP BY department_id` jest błędne, bo w jednym dziale jest wielu `first_name`.)

* **`WHERE` vs `HAVING` — kluczowa różnica.** `WHERE` filtruje **pojedyncze wiersze przed** zgrupowaniem (np. „weź tylko aktywnych”). `HAVING` filtruje **całe grupy po** agregacji (np. „pokaż tylko działy mające > 2 pracowników”). W `HAVING` możesz użyć funkcji agregującej (`HAVING COUNT(*) > 2`), w `WHERE` — nie. Reguła: warunek o pojedynczym wierszu → `WHERE`; warunek o wyniku agregacji → `HAVING`.

* **Kolejność logicznego wykonania zapytania.** To dlatego aliasu z `SELECT` nie widać w `WHERE`:
  `FROM` (skąd) → `WHERE` (które wiersze) → `GROUP BY` (kubełki) → `HAVING` (które kubełki) → `SELECT` (które kolumny/wyrażenia) → `ORDER BY` (kolejność) → `LIMIT` (ile). Piszemy `SELECT` na górze, ale baza wykonuje go prawie na końcu.

* **`COUNT(*)` vs `COUNT(kolumna)` vs `COUNT(DISTINCT kolumna)`.** `COUNT(*)` liczy *wiersze* (także te z samymi `NULL`). `COUNT(phone)` liczy tylko wiersze, gdzie `phone` **nie jest** `NULL`. `COUNT(DISTINCT salary)` liczy *różne* wartości. Różnica między `COUNT(*)` a `COUNT(kol)` to ulubione pytanie na rozmowach.

* **Agregacja a `NULL`.** Funkcje (poza `COUNT(*)`) **pomijają** `NULL`-e. Dlatego `AVG(salary)` to średnia z wierszy mających pensję, a na pustym zbiorze zwraca `NULL` (a nie `0`) — bo „średnia z niczego” jest nieokreślona. `SUM` z samych `NULL` też daje `NULL`. Często ratuje `COALESCE(SUM(x), 0)`.

* **`FILTER (WHERE ...)`** (PostgreSQL) — pozwala policzyć kilka różnych warunków w jednym przebiegu: `COUNT(*) FILTER (WHERE is_active)` obok `COUNT(*)`. Czytelniej i szybciej niż kilka osobnych zapytań.

### Schemat SQL

Używamy tabel `employees` i `departments` z sekcji 4.

### Zadania do wykonania

1. **Liczba wszystkich pracowników**:

   ```sql
   SELECT COUNT(*) FROM employees;
   ```

2. **Liczba aktywnych pracowników**:

   ```sql
   SELECT COUNT(*) FROM employees WHERE is_active = TRUE;
   ```

3. **Liczba pracowników z numerem telefonu** — `COUNT(phone)` (zlicza nie-NULL):

   ```sql
   SELECT COUNT(phone) AS with_phone, COUNT(*) AS total FROM employees;
   ```

4. **Suma pensji w firmie**:

   ```sql
   SELECT SUM(salary) AS total_payroll FROM employees;
   ```

5. **Średnia, minimalna i maksymalna pensja**:

   ```sql
   SELECT AVG(salary) AS srednia, MIN(salary) AS min, MAX(salary) AS max
   FROM employees;
   ```

6. **Średnia pensja zaokrąglona do 2 miejsc**:

   ```sql
   SELECT ROUND(AVG(salary), 2) AS srednia_pensja FROM employees;
   ```

7. **`GROUP BY department_id` — liczba pracowników w dziale**:

   ```sql
   SELECT department_id, COUNT(*) AS liczba_pracownikow
   FROM employees
   GROUP BY department_id
   ORDER BY department_id;
   ```

8. **Suma pensji per dział**:

   ```sql
   SELECT department_id, SUM(salary) AS budzet_pensji
   FROM employees
   WHERE department_id IS NOT NULL
   GROUP BY department_id;
   ```

9. **Średnia pensja per dział, posortowane malejąco**:

   ```sql
   SELECT department_id, ROUND(AVG(salary), 2) AS srednia
   FROM employees
   GROUP BY department_id
   ORDER BY srednia DESC NULLS LAST;
   ```

10. **`HAVING` — działy z więcej niż 2 pracownikami**:

    ```sql
    SELECT department_id, COUNT(*) AS liczba
    FROM employees
    GROUP BY department_id
    HAVING COUNT(*) > 2;
    ```

11. **`HAVING` z `SUM`** — działy gdzie sumaryczna pensja > 30000:

    ```sql
    SELECT department_id, SUM(salary) AS suma
    FROM employees
    GROUP BY department_id
    HAVING SUM(salary) > 30000;
    ```

12. **Liczba zatrudnionych w każdym roku**:

    ```sql
    SELECT EXTRACT(YEAR FROM hire_date) AS rok, COUNT(*) AS liczba
    FROM employees
    GROUP BY EXTRACT(YEAR FROM hire_date)
    ORDER BY rok;
    ```

13. **`COUNT(DISTINCT ...)` — ile unikalnych pensji jest w firmie?**

    ```sql
    SELECT COUNT(DISTINCT salary) AS unikalne_pensje FROM employees;
    ```

14. **`COUNT(DISTINCT)` per dział** — ilu unikalnych managerów ma każdy dział:

    ```sql
    SELECT department_id, COUNT(DISTINCT manager_id) AS unikalni_managerowie
    FROM employees
    WHERE manager_id IS NOT NULL
    GROUP BY department_id;
    ```

15. **Łączenie `WHERE`, `GROUP BY`, `HAVING`, `ORDER BY`, `LIMIT`** — top 3 działy z największą sumą pensji, ale tylko wśród aktywnych pracowników:

    ```sql
    SELECT department_id, SUM(salary) AS suma_pensji, COUNT(*) AS liczba_aktywnych
    FROM employees
    WHERE is_active = TRUE
    GROUP BY department_id
    HAVING COUNT(*) >= 2
    ORDER BY suma_pensji DESC
    LIMIT 3;
    ```

16. **`FILTER (WHERE ...)`** — PostgreSQL pozwala filtrować w obrębie agregacji:

    ```sql
    SELECT
        department_id,
        COUNT(*) AS wszyscy,
        COUNT(*) FILTER (WHERE is_active = TRUE) AS aktywni,
        COUNT(*) FILTER (WHERE is_active = FALSE) AS nieaktywni,
        ROUND(AVG(salary) FILTER (WHERE is_active = TRUE), 2) AS srednia_aktywnych
    FROM employees
    GROUP BY department_id;
    ```

17. **Grupowanie po wielu kolumnach** — liczba pracowników per (`department_id`, `is_active`):

    ```sql
    SELECT department_id, is_active, COUNT(*) AS liczba
    FROM employees
    GROUP BY department_id, is_active
    ORDER BY department_id, is_active;
    ```

18. **`GROUPING SETS` / `ROLLUP`** — podsumowania częściowe:

    ```sql
    SELECT department_id, is_active, COUNT(*) AS liczba
    FROM employees
    GROUP BY ROLLUP (department_id, is_active);
    -- ROLLUP daje wyniki: per (dept, active), per dept, oraz total
    ```

### Pytania kontrolne

1. Czym `WHERE` różni się od `HAVING`?
2. Czemu `SELECT id, COUNT(*) FROM employees GROUP BY department_id;` zwróci błąd?
3. Co liczy `COUNT(*)`, a co `COUNT(phone)` przy 12 wierszach z 8 nie-NULL telefonami?
4. Czemu `AVG(salary)` na pustej tabeli zwraca `NULL`, a nie `0`?
5. Kiedy `HAVING` może istnieć bez `GROUP BY`?
6. Co robi `COUNT(DISTINCT manager_id)`?
7. Czym `FILTER (WHERE ...)` różni się od ogólnego `WHERE` w tym samym zapytaniu?

---

## 7. Łączenie tabel — `INNER JOIN`, `LEFT JOIN`, `RIGHT JOIN`, `FULL JOIN`, `CROSS JOIN`, self-join

**Cel:** Łączyć dane z wielu tabel w jedną wyniko, używając właściwego typu JOIN-a.

**Teoria w pigułce:** `INNER JOIN` — tylko pasujące wiersze z obu stron. `LEFT JOIN` — wszystkie z lewej + dopasowane z prawej (brakujące jako NULL). `RIGHT JOIN` — odwrotnie. `FULL OUTER JOIN` — wszystkie z obu stron. `CROSS JOIN` — iloczyn kartezjański (każdy z każdym). Self-join — tabela złączona ze sobą (np. pracownik z managerem z tej samej tabeli `employees`).

**Teoria szerzej — przeczytaj zanim zaczniesz pisać:**

* **Po co `JOIN`?** W dobrze zaprojektowanej bazie dane są **rozbite na tabele** (normalizacja), żeby nie powtarzać tych samych informacji. Nazwa działu leży raz, w `departments`; pracownik trzyma tylko `department_id`. Żeby pokazać „imię pracownika i nazwę jego działu”, trzeba te tabele *z powrotem skleić* — to robi `JOIN`. To najważniejsza operacja w bazach relacyjnych.

* **Warunek złączenia (`ON`).** `JOIN` mówi *które tabele* łączymy, a `ON` mówi *po czym*: `ON e.department_id = d.id` — sklej wiersz pracownika z wierszem działu, gdy klucz obcy pasuje do klucza głównego. Bez sensownego `ON` (albo z błędnym) dostaniesz iloczyn kartezjański — każdy z każdym.

* **`INNER` vs `LEFT` — to decyzja, nie ozdoba.** `INNER JOIN` zwraca **tylko** wiersze, które mają parę po obu stronach. Pracownik bez działu *zniknie* z wyniku. `LEFT JOIN` zachowuje **wszystkie** wiersze z lewej tabeli, a gdy brak pary po prawej — wstawia `NULL`. Pytanie kontrolne, które zawsze sobie zadawaj: „czy chcę też wiersze bez dopasowania?” Tak → `LEFT JOIN`. Nie → `INNER JOIN`.

* **Wzorzec „znajdź sieroty”.** `LEFT JOIN` + `WHERE prawa.klucz IS NULL` zwraca wiersze z lewej, które **nie mają** pary po prawej: „działy bez pracowników”, „książki nigdy nie zamówione”, „klienci bez zamówień”. Bardzo częsty i bardzo użyteczny idiom.

* **`RIGHT` i `FULL`.** `RIGHT JOIN` to `LEFT JOIN` „na odwrót” (wszystkie z prawej) — w praktyce rzadki, bo zwykle wygodniej zamienić tabele miejscami i użyć `LEFT`. `FULL OUTER JOIN` zachowuje wszystkie wiersze z **obu** stron, wstawiając `NULL` tam, gdzie brak pary.

* **`COUNT(*)` po `LEFT JOIN` kłamie.** Po `LEFT JOIN` dział bez pracowników daje *jeden* wiersz z samymi `NULL` po stronie pracownika. `COUNT(*)` policzy go jako 1 (bo wiersz istnieje), a `COUNT(e.id)` jako 0 (bo `e.id` jest `NULL`). Do liczenia „ilu naprawdę pracowników” używaj `COUNT(kolumna_z_prawej_tabeli)`, nie `COUNT(*)`.

* **Self-join.** Tabela złączona sama ze sobą pod dwoma aliasami. Pracownik i jego przełożony siedzą w tej samej tabeli `employees` — żeby pokazać „pracownik → manager”, łączymy `employees e` z `employees m` po `e.manager_id = m.id`. Alias jest tu obowiązkowy, bo inaczej baza nie wie, o którą „kopię” tabeli chodzi.

* **`CROSS JOIN` — iloczyn kartezjański.** Każdy wiersz z lewej z każdym z prawej (12 × 5 = 60). Świadomie używany rzadko (np. generowanie kombinacji), ale **przypadkowo** powstaje, gdy zapomnisz `ON` — i wtedy wynik nagle „puchnie” do tysięcy wierszy. To sygnał ostrzegawczy.

* **Aliasy tabel.** Przy `JOIN`-ach nadawaj tabelom krótkie aliasy (`employees e`, `departments d`) i prefiksuj kolumny (`e.salary`, `d.name`). Przy kolumnach o tej samej nazwie w obu tabelach (np. `id`) prefiks jest *obowiązkowy*, inaczej „ambiguous column”.

### Schemat SQL

Używamy tabel `employees` i `departments` z sekcji 4, plus tabel `publishers`, `authors`, `books`, `book_authors` z sekcji 1 oraz `book_orders` z sekcji 3.

### Zadania do wykonania

1. **`INNER JOIN` — pracownicy z nazwą działu**:

   ```sql
   SELECT e.first_name, e.last_name, d.name AS dzial, d.city
   FROM employees e
   INNER JOIN departments d ON e.department_id = d.id;
   ```

2. **Czemu pracownik Paweł Jankowski nie jest na liście z punktu 1?** Sprawdź `department_id` u Pawła.
3. **`LEFT JOIN` — wszyscy pracownicy, nawet bez działu**:

   ```sql
   SELECT e.first_name, e.last_name, d.name AS dzial
   FROM employees e
   LEFT JOIN departments d ON e.department_id = d.id
   ORDER BY e.id;
   ```

4. **`RIGHT JOIN` — wszystkie działy, nawet bez pracowników**:

   ```sql
   SELECT d.name AS dzial, e.first_name, e.last_name
   FROM employees e
   RIGHT JOIN departments d ON e.department_id = d.id
   ORDER BY d.name;
   ```

5. **`FULL OUTER JOIN` — wszystko ze wszystkich tabel**:

   ```sql
   SELECT d.name AS dzial, e.first_name, e.last_name
   FROM departments d
   FULL OUTER JOIN employees e ON e.department_id = d.id;
   ```

6. **Działy bez pracowników** (wskazówka: `LEFT JOIN` + `WHERE ... IS NULL`):

   ```sql
   SELECT d.name FROM departments d
   LEFT JOIN employees e ON e.department_id = d.id
   WHERE e.id IS NULL;
   ```

7. **Pracownicy bez działu** (analogicznie):

   ```sql
   SELECT e.first_name, e.last_name FROM employees e
   LEFT JOIN departments d ON e.department_id = d.id
   WHERE d.id IS NULL;
   ```

8. **`COUNT(*)` vs `COUNT(e.id)` po `LEFT JOIN`**:

   ```sql
   SELECT d.name,
          COUNT(*)   AS chytry,
          COUNT(e.id) AS prawdziwy
   FROM departments d
   LEFT JOIN employees e ON e.department_id = d.id
   GROUP BY d.id, d.name;
   ```

   Zobacz różnicę dla działu bez pracowników — `COUNT(*)` daje 1 (jeden wiersz z NULL-ami), `COUNT(e.id)` daje 0 (kolumna e.id jest NULL).

9. **`CROSS JOIN`** — każda kombinacja pracownik × dział (głównie do demo, na produkcji rzadko):

   ```sql
   SELECT e.first_name, d.name FROM employees e CROSS JOIN departments d;
   ```

   Pomnóż 12 × 5 = 60 wierszy.

10. **Self-join — pracownik z imieniem swojego managera**:

    ```sql
    SELECT
        e.first_name || ' ' || e.last_name AS pracownik,
        m.first_name || ' ' || m.last_name AS manager
    FROM employees e
    LEFT JOIN employees m ON e.manager_id = m.id
    ORDER BY manager;
    ```

11. **`JOIN` trzech tabel** — książki z nazwą wydawcy i nazwiskiem autora:

    ```sql
    SELECT
        b.title,
        p.name AS wydawca,
        a.first_name || ' ' || a.last_name AS autor
    FROM books b
    INNER JOIN publishers p ON b.publisher_id = p.id
    INNER JOIN book_authors ba ON ba.book_id = b.id
    INNER JOIN authors a ON ba.author_id = a.id
    ORDER BY b.title;
    ```

12. **Książki z kilkoma autorami** — które książki napisało co najmniej 2 autorów?

    ```sql
    SELECT b.title, COUNT(ba.author_id) AS liczba_autorow
    FROM books b
    JOIN book_authors ba ON ba.book_id = b.id
    GROUP BY b.id, b.title
    HAVING COUNT(ba.author_id) >= 2;
    ```

13. **Klienci i ich zakupy** — wszystkie zamówienia z tytułem książki:

    ```sql
    SELECT
        bo.customer_name,
        b.title,
        bo.quantity,
        bo.unit_price,
        bo.quantity * bo.unit_price AS wartosc,
        bo.status
    FROM book_orders bo
    JOIN books b ON bo.book_id = b.id
    ORDER BY bo.ordered_at DESC;
    ```

14. **Top 3 autorów** po sumarycznej liczbie zamówień ich książek:

    ```sql
    SELECT
        a.first_name || ' ' || a.last_name AS autor,
        SUM(bo.quantity) AS sprzedanych_sztuk
    FROM authors a
    JOIN book_authors ba ON ba.author_id = a.id
    JOIN books b ON ba.book_id = b.id
    JOIN book_orders bo ON bo.book_id = b.id
    WHERE bo.status IN ('PAID', 'SHIPPED')
    GROUP BY a.id, a.first_name, a.last_name
    ORDER BY sprzedanych_sztuk DESC
    LIMIT 3;
    ```

15. **Książki które jeszcze nigdy nie były zamawiane** (`LEFT JOIN` + `IS NULL`):

    ```sql
    SELECT b.title FROM books b
    LEFT JOIN book_orders bo ON bo.book_id = b.id
    WHERE bo.id IS NULL;
    ```

16. **`USING (kolumna)`** — skrócona składnia gdy obie tabele mają kolumnę o tej samej nazwie:

    ```sql
    -- USING (book_id) działa, bo OBIE tabele mają kolumnę o nazwie "book_id":
    SELECT bo.id, bo.customer_name, ba.author_id
    FROM book_orders bo
    INNER JOIN book_authors ba USING (book_id);
    ```

    Uwaga: `USING` zadziała tylko, gdy łączące kolumny mają **tę samą nazwę** w obu tabelach. Gdy nazwy się różnią (np. `books.id` vs `book_orders.book_id`), `USING` nie przejdzie — wtedy trzeba klasycznego `ON book_orders.book_id = books.id`.

17. **`NATURAL JOIN`** — automatycznie łączy po kolumnach o tej samej nazwie (raczej unikaj, niejawne):

    ```sql
    SELECT * FROM employees NATURAL JOIN departments;
    -- to pewnie zwróci 0 wierszy bo wspólnych kolumn brak
    ```

18. **Manager każdego pracownika + nazwa działu managera** — self-join + JOIN z `departments`:

    ```sql
    SELECT
        e.first_name || ' ' || e.last_name AS pracownik,
        m.first_name || ' ' || m.last_name AS manager,
        d.name AS dzial_managera
    FROM employees e
    LEFT JOIN employees m ON e.manager_id = m.id
    LEFT JOIN departments d ON m.department_id = d.id;
    ```

### Pytania kontrolne

1. Czym różni się `INNER JOIN` od `LEFT JOIN`?
2. Co zwróci `LEFT JOIN` jeśli z prawej strony nie ma dopasowania?
3. Czemu `RIGHT JOIN` jest rzadko używany w praktyce?
4. Kiedy ma sens `FULL OUTER JOIN`?
5. Po co aliasy `employees e`, `departments d`?
6. Co robi `JOIN ... USING (id)` vs `JOIN ... ON a.id = b.id`?
7. Jaka jest typowa liczba wierszy w `CROSS JOIN` z dwóch tabel n=10 i m=5?
8. Co to "self-join" i kiedy go używamy?

---

## 8. Podzapytania (subqueries) — w `WHERE`, `FROM`, `SELECT`; korelowane vs niekorelowane

**Cel:** Wykorzystywać wynik jednego zapytania w drugim.

**Teoria w pigułce:** Subquery to zapytanie SELECT w nawiasach, używane jako:
* **Scalar subquery** — zwraca jedną wartość, używana jak liczba (`WHERE price > (SELECT AVG(price) FROM books)`).
* **Subquery w FROM** — wynik jako "tabela tymczasowa", musi mieć alias.
* **Subquery w SELECT** — kolumna obliczana per wiersz.
* **`IN (subquery)`** — wartość z listy zwróconej przez subquery.
* **`EXISTS (subquery)`** — czy subquery zwraca cokolwiek.

**Korelowane** subquery odwołuje się do kolumn z głównego zapytania — wykonywane raz na każdy wiersz. **Niekorelowane** są niezależne i wykonywane raz.

**Teoria szerzej — przeczytaj zanim zaczniesz pisać:**

* **Po co podzapytania?** Czasem żeby coś przefiltrować, najpierw trzeba coś *policzyć*. „Pracownicy zarabiający powyżej średniej” — średnia to osobne zapytanie, którego wynik wkładamy do warunku głównego. Podzapytanie pozwala użyć **wyniku jednego SELECT-a wewnątrz drugiego**, bez zapisywania pośrednich tabel.

* **Skalarne podzapytanie zwraca jedną wartość** i może stać wszędzie tam, gdzie pasuje pojedyncza liczba: `WHERE salary > (SELECT AVG(salary) FROM employees)`. Jeśli takie podzapytanie zwróci przypadkiem więcej niż jeden wiersz — błąd. Gdy spodziewasz się listy, użyj `IN`, nie `=`.

* **Korelowane vs niekorelowane — różnica wydajnościowa.** *Niekorelowane* jest samodzielne: baza liczy je **raz** i wstawia wynik. *Korelowane* odwołuje się do kolumny z zapytania zewnętrznego (`WHERE e2.department_id = e.department_id`) — wykonuje się **dla każdego wiersza** zewnętrznego, jak pętla w pętli. Bywa wolniejsze; często da się je przepisać na `JOIN`.

* **`IN (subquery)` vs `EXISTS (subquery)`.** `IN` sprawdza, czy wartość jest na liście zwróconej przez podzapytanie. `EXISTS` sprawdza tylko, *czy podzapytanie w ogóle coś zwraca* (nie liczy wierszy — przerywa na pierwszym trafieniu, dlatego często szybsze). W `EXISTS` pisze się zwyczajowo `SELECT 1`, bo i tak nie liczy się, *co* zwraca, tylko *czy*.

* **`NOT IN` + `NULL` = pułapka.** Jeśli podzapytanie w `NOT IN` zwróci choć jeden `NULL`, całość przez logikę trójwartościową zwykle daje **zero wierszy** — wynik, którego nikt się nie spodziewa. Bezpieczna alternatywa: `NOT EXISTS`, które radzi sobie z `NULL` poprawnie.

* **Podzapytanie w `FROM` (tzw. derived table).** Wynik `SELECT`-a użyty jako tymczasowa tabela — *musi* mieć alias (`) AS sub`). Klasyk: „policz coś per grupa, a potem zagreguj te wyniki” (np. maksimum ze średnich działowych) — robi się to w dwóch warstwach, wewnętrznej i zewnętrznej.

* **`ANY` / `ALL`.** `> ALL (...)` = większy niż **wszystkie** zwrócone wartości (czyli niż maksimum). `> ANY (...)` = większy niż **przynajmniej jedna** (czyli niż minimum). Mniej czytelne niż `MAX`/`MIN`, ale warto rozpoznawać.

* **Podzapytanie vs `JOIN`.** Wiele rzeczy da się zapisać oboma sposobami. Z grubsza: gdy chcesz *kolumny* z drugiej tabeli — `JOIN`. Gdy chcesz tylko *sprawdzić istnienie* albo *porównać z wyliczoną wartością* — podzapytanie (`EXISTS`, skalarne) bywa czytelniejsze. Optymalizator PostgreSQL i tak często sprowadza je do tego samego planu.

### Schemat SQL

Używamy schematów z poprzednich sekcji.

### Zadania do wykonania

1. **Pracownicy z pensją powyżej średniej** (scalar subquery niekorelowane):

   ```sql
   SELECT first_name, last_name, salary FROM employees
   WHERE salary > (SELECT AVG(salary) FROM employees);
   ```

2. **Pracownicy z pensją równą maksymalnej**:

   ```sql
   SELECT first_name, last_name, salary FROM employees
   WHERE salary = (SELECT MAX(salary) FROM employees);
   ```

3. **Pracownicy z działu z największym budżetem**:

   ```sql
   SELECT first_name, last_name FROM employees
   WHERE department_id = (
       SELECT id FROM departments ORDER BY budget DESC LIMIT 1
   );
   ```

4. **Subquery w `SELECT`** — przy każdym pracowniku dopisz średnią pensję firmy:

   ```sql
   SELECT
       first_name, last_name, salary,
       (SELECT ROUND(AVG(salary), 2) FROM employees) AS srednia_firmy
   FROM employees;
   ```

5. **Korelowane subquery** — przy każdym pracowniku jego pensja minus średnia jego działu:

   ```sql
   SELECT
       e.first_name, e.last_name, e.department_id, e.salary,
       (SELECT ROUND(AVG(salary), 2)
        FROM employees e2
        WHERE e2.department_id = e.department_id) AS srednia_dzialu,
       e.salary - (SELECT AVG(salary)
                   FROM employees e2
                   WHERE e2.department_id = e.department_id) AS roznica
   FROM employees e
   WHERE e.department_id IS NOT NULL;
   ```

6. **`IN (subquery)`** — pracownicy z działów w Warszawie:

   ```sql
   SELECT first_name, last_name FROM employees
   WHERE department_id IN (
       SELECT id FROM departments WHERE city = 'Warszawa'
   );
   ```

7. **`NOT IN (subquery)`** — pracownicy NIE z Warszawy:

   ```sql
   SELECT first_name, last_name FROM employees
   WHERE department_id NOT IN (
       SELECT id FROM departments WHERE city = 'Warszawa'
   );
   ```

   Uwaga — jeśli subquery zwróci NULL, `NOT IN` zwróci 0 wierszy. Bezpieczniej użyć `NOT EXISTS`.

8. **`EXISTS (subquery)`** — klienci którzy złożyli przynajmniej jedno zamówienie PAID:

   ```sql
   SELECT DISTINCT customer_name FROM book_orders bo1
   WHERE EXISTS (
       SELECT 1 FROM book_orders bo2
       WHERE bo2.customer_name = bo1.customer_name AND bo2.status = 'PAID'
   );
   ```

9. **`NOT EXISTS (subquery)`** — książki które nigdy nie były zamawiane:

   ```sql
   SELECT b.title FROM books b
   WHERE NOT EXISTS (
       SELECT 1 FROM book_orders bo WHERE bo.book_id = b.id
   );
   ```

10. **Subquery w `FROM`** — średnia pensja per dział, potem maksymalna z tych średnich:

    ```sql
    SELECT MAX(srednia) AS max_srednia
    FROM (
        SELECT department_id, AVG(salary) AS srednia
        FROM employees
        GROUP BY department_id
    ) AS srednie;
    ```

11. **`ANY` / `ALL`** — pracownicy zarabiający więcej niż **wszyscy** w dziale 4:

    ```sql
    SELECT first_name, last_name, salary FROM employees
    WHERE salary > ALL (
        SELECT salary FROM employees WHERE department_id = 4
    );
    ```

12. **`ANY`** — pracownicy zarabiający więcej niż **dowolny** z działu 2:

    ```sql
    SELECT first_name, last_name, salary FROM employees
    WHERE salary > ANY (
        SELECT salary FROM employees WHERE department_id = 2
    );
    ```

13. **Top 3 najdroższe książki każdego wydawcy** — przybliżenie przez korelowane subquery (eleganckie rozwiązanie tego problemu dają funkcje okienkowe, które wykraczają poza tę kartę — tu liczymy „ile książek tego wydawcy jest droższych”):

    ```sql
    SELECT b1.title, b1.publisher_id, b1.price
    FROM books b1
    WHERE (
        SELECT COUNT(*) FROM books b2
        WHERE b2.publisher_id = b1.publisher_id AND b2.price > b1.price
    ) < 3
    ORDER BY b1.publisher_id, b1.price DESC;
    ```

14. **Średnia liczba zamówień na klienta**:

    ```sql
    SELECT AVG(liczba_zamowien) AS srednia_zamowien_per_klient
    FROM (
        SELECT customer_name, COUNT(*) AS liczba_zamowien
        FROM book_orders
        GROUP BY customer_name
    ) sub;
    ```

15. **Pracownicy zarabiający więcej niż średnia ich managera** — korelowane subquery:

    ```sql
    SELECT e.first_name, e.last_name, e.salary, e.manager_id
    FROM employees e
    WHERE e.manager_id IS NOT NULL
      AND e.salary > (SELECT salary FROM employees WHERE id = e.manager_id);
    ```

### Pytania kontrolne

1. Czym scalar subquery różni się od subquery zwracającego wiele wierszy?
2. Czym korelowane subquery różni się od niekorelowanego?
3. Czemu `EXISTS` jest często szybsze niż `IN (subquery)`?
4. Dlaczego `NOT IN (subquery)` z NULL-em zachowuje się nieintuicyjnie?
5. Kiedy lepiej użyć JOIN-a zamiast subquery? Co decyduje?
6. Co robi `WHERE x > ALL (SELECT y FROM ...)`?
7. Czemu subquery w `FROM` zawsze musi mieć alias?

---

## 9. Indeksy — `CREATE INDEX`, kiedy używać, `EXPLAIN`

**Cel:** Tworzyć indeksy w odpowiednich miejscach, analizować plany zapytań, przyspieszać wolne zapytania.

**Teoria w pigułce:** Indeks to dodatkowa struktura (najczęściej B-drzewo) trzymana obok tabeli, pozwalająca szybko znaleźć wiersze po kluczu. Bez indeksu — pełny skan tabeli `O(n)`. Z indeksem — `O(log n)`. Kompromis: indeksy zajmują dysk i spowalniają `INSERT`/`UPDATE`/`DELETE` (każda modyfikacja musi też zaktualizować indeks). Indeksuj kolumny używane w `WHERE`, `JOIN ON`, `ORDER BY`. **Nie** indeksuj małych tabel, kolumn z niską selektywnością (BOOLEAN), kolumn często aktualizowanych. `PRIMARY KEY` i `UNIQUE` są **automatycznie** indeksowane. FK w PostgreSQL **NIE** są automatycznie indeksowane — musisz dodać ręcznie.

**Teoria szerzej — przeczytaj zanim zaczniesz pisać:**

* **Analogia: indeks w książce.** Żeby znaleźć słowo w 800-stronicowej książce bez skorowidza, czytasz stronę po stronie (pełny skan, `Seq Scan`). Ze skorowidzem na końcu skaczesz od razu na właściwą stronę (`Index Scan`). Indeks w bazie to dokładnie taki skorowidz — posortowana struktura, która mówi „wiersze z `customer_id = 42` leżą tu i tu”.

* **Jak działa B-drzewo.** Domyślny indeks PostgreSQL to zrównoważone drzewo wyszukiwań. Zamiast przejrzeć milion wierszy, baza schodzi w dół drzewa, dzieląc zbiór na pół na każdym poziomie — stąd `O(log n)`. Milion wierszy to ~20 kroków zamiast miliona. B-drzewo wspiera `=`, `<`, `>`, `BETWEEN`, `ORDER BY` i prefiksy `LIKE 'abc%'`.

* **Indeksy nie są darmowe — to kompromis.** Każdy indeks (1) zajmuje miejsce na dysku i (2) musi być aktualizowany przy **każdym** `INSERT`/`UPDATE`/`DELETE` na indeksowanej kolumnie. Indeks przyspiesza odczyt, ale spowalnia zapis. Dlatego nie indeksuje się „wszystkiego na wszelki wypadek” — tylko kolumny, po których naprawdę filtrujesz/łączysz/sortujesz.

* **Selektywność — najważniejsze kryterium.** Indeks opłaca się, gdy zawęża wynik do *małego ułamka* tabeli. Kolumna `email` (prawie same różne wartości) — świetna do indeksu. Kolumna `is_active BOOLEAN` (tylko 2 wartości, połowa tu, połowa tam) — indeks nic nie da, bo i tak trzeba przeczytać pół tabeli; baza go zignoruje i zrobi `Seq Scan`.

* **`EXPLAIN` i `EXPLAIN ANALYZE` — Twój rentgen.** `EXPLAIN` pokazuje **plan**, który baza *zamierza* wykonać (bez uruchamiania). `EXPLAIN ANALYZE` faktycznie **wykonuje** zapytanie i podaje realne czasy. Szukasz słów: `Seq Scan` (pełny skan — podejrzane przy dużych tabelach) vs `Index Scan` (użyto indeksu). To jedyny wiarygodny sposób sprawdzenia, *czy indeks naprawdę pomaga* — zgadywanie zwykle zawodzi.

* **Indeks złożony i kolejność kolumn ma znaczenie.** `INDEX (region, sale_date)` wspiera zapytania po `region` oraz po `region AND sale_date`, ale **nie** po samym `sale_date` (jak skorowidz „miasto, ulica” — bez miasta nie wyszukasz). Reguła: najbardziej selektywna / najczęściej używana kolumna z lewej.

* **Indeks częściowy i funkcyjny.** *Częściowy* (`WHERE status = 'PAID'`) indeksuje tylko podzbiór wierszy — mniejszy, szybszy, gdy pytasz głównie o ten podzbiór. *Funkcyjny* (`LOWER(email)`) indeksuje wynik wyrażenia — konieczny, by przyspieszyć `WHERE LOWER(email) = ...` (zwykły indeks na `email` tu nie zadziała).

* **FK nie jest indeksowany automatycznie!** PostgreSQL zakłada indeks dla `PRIMARY KEY` i `UNIQUE`, ale **nie** dla kolumn `FOREIGN KEY`. Brak indeksu na kluczu obcym to częsta przyczyna wolnych `JOIN`-ów i wolnego kasowania rodzica (baza skanuje całą tabelę dzieci). To dlatego w konwencjach tej karty piszemy: *klucze obce indeksujemy ręcznie*.

### Schemat SQL

Stwórzmy większą tabelę do testów wydajnościowych:

```sql
DROP TABLE IF EXISTS sales;

CREATE TABLE sales (
    id          BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id BIGINT       NOT NULL,
    product_id  BIGINT       NOT NULL,
    quantity    INTEGER      NOT NULL,
    price       NUMERIC(10,2) NOT NULL,
    sale_date   DATE         NOT NULL,
    region      VARCHAR(30)  NOT NULL,
    status      VARCHAR(20)  NOT NULL
);

-- Wstaw 100 000 wierszy losowych
INSERT INTO sales (customer_id, product_id, quantity, price, sale_date, region, status)
SELECT
    (RANDOM() * 1000)::BIGINT + 1,
    (RANDOM() * 100)::BIGINT + 1,
    (RANDOM() * 10)::INT + 1,
    (RANDOM() * 1000 + 10)::NUMERIC(10,2),
    DATE '2020-01-01' + (RANDOM() * 1500)::INT,
    (ARRAY['Warszawa','Kraków','Wrocław','Poznań','Gdańsk'])[FLOOR(RANDOM() * 5)::INT + 1],
    (ARRAY['NEW','PAID','SHIPPED','CANCELLED'])[FLOOR(RANDOM() * 4)::INT + 1]
FROM generate_series(1, 100000);
```

### Zadania do wykonania

1. **`EXPLAIN` bez indeksu** — sprawdź jak baza wykonuje zapytanie:

   ```sql
   EXPLAIN SELECT * FROM sales WHERE customer_id = 42;
   ```

   Zobaczysz `Seq Scan` — pełny skan tabeli.

2. **`EXPLAIN ANALYZE`** — to samo, ale faktycznie wykonuje zapytanie i mierzy czas:

   ```sql
   EXPLAIN ANALYZE SELECT * FROM sales WHERE customer_id = 42;
   ```

   Zobaczysz `actual time=X ms`.

3. **Stwórz indeks na `customer_id`**:

   ```sql
   CREATE INDEX idx_sales_customer ON sales(customer_id);
   ```

4. **`EXPLAIN ANALYZE` po dodaniu indeksu** — porównaj czas:

   ```sql
   EXPLAIN ANALYZE SELECT * FROM sales WHERE customer_id = 42;
   ```

   Powinieneś zobaczyć `Index Scan using idx_sales_customer`, czas znacznie krótszy.

5. **Indeks na dwie kolumny (composite)**:

   ```sql
   CREATE INDEX idx_sales_region_date ON sales(region, sale_date);
   ```

   Sprawdź plan dla zapytania:

   ```sql
   EXPLAIN ANALYZE SELECT * FROM sales WHERE region = 'Warszawa' AND sale_date > '2023-01-01';
   ```

6. **Indeks częściowy (partial index)** — tylko dla statusu PAID:

   ```sql
   CREATE INDEX idx_sales_paid ON sales(customer_id) WHERE status = 'PAID';
   ```

   Sprawdź plan:

   ```sql
   EXPLAIN ANALYZE SELECT * FROM sales WHERE customer_id = 42 AND status = 'PAID';
   ```

7. **Indeks unikalny** — wymusza unikalność wartości:

   ```sql
   CREATE UNIQUE INDEX idx_employees_email ON employees(LOWER(email));
   -- po LOWER, więc 'Anna@firma.pl' i 'anna@firma.pl' będą uznane za to samo
   ```

8. **Indeks po wyrażeniu (functional index)**:

   ```sql
   CREATE INDEX idx_employees_lastname_lower ON employees(LOWER(last_name));
   -- przyspieszy zapytania typu WHERE LOWER(last_name) = 'kowalski'
   ```

9. **Lista wszystkich indeksów** w `psql`:

   ```
   \di
   ```

   lub przez katalog systemowy:

   ```sql
   SELECT indexname, tablename, indexdef
   FROM pg_indexes
   WHERE schemaname = 'public'
   ORDER BY tablename, indexname;
   ```

10. **Rozmiar indeksów**:

    ```sql
    SELECT
        indexname,
        pg_size_pretty(pg_relation_size(indexname::REGCLASS)) AS rozmiar
    FROM pg_indexes
    WHERE schemaname = 'public'
    ORDER BY pg_relation_size(indexname::REGCLASS) DESC;
    ```

11. **Usuń indeks**:

    ```sql
    DROP INDEX IF EXISTS idx_sales_customer;
    ```

12. **`REINDEX`** — odbudowa indeksu:

    ```sql
    REINDEX TABLE sales;
    ```

13. **`VACUUM ANALYZE`** — aktualizacja statystyk planera:

    ```sql
    VACUUM ANALYZE sales;
    ```

14. **`EXPLAIN (ANALYZE, BUFFERS)`** — z informacją o buforach:

    ```sql
    EXPLAIN (ANALYZE, BUFFERS) SELECT * FROM sales WHERE customer_id = 42;
    ```

15. **Test wydajności bez indeksu vs z indeksem** — porównaj `actual time` dla:
    * `WHERE customer_id = X`
    * `WHERE region = 'Warszawa'`
    * `WHERE sale_date BETWEEN '2023-01-01' AND '2023-12-31'`
    * `WHERE customer_id = 42 AND region = 'Warszawa'`

    Wszystkie powinny przyspieszyć po dodaniu odpowiednich indeksów.

### Pytania kontrolne

1. Co robi indeks B-drzewa pod spodem?
2. Czemu indeksy nie są darmowe?
3. Które kolumny **warto** indeksować?
4. Które kolumny **nie warto** indeksować?
5. Czemu `PRIMARY KEY` ma automatyczny indeks, a FK nie?
6. Czym `EXPLAIN` różni się od `EXPLAIN ANALYZE`?
7. Co to indeks częściowy i kiedy się przydaje?
8. Co to indeks funkcyjny (expression index)?

---

## 10. Widoki (`VIEW`) i widoki zmaterializowane (`MATERIALIZED VIEW`)

**Cel:** Tworzyć i używać widoków jako abstrakcji nad złożonymi zapytaniami. Rozumieć różnicę między `VIEW` a `MATERIALIZED VIEW`.

**Teoria w pigułce:** `VIEW` to **zapisane zapytanie** które można potem używać jak tabelę. Nie przechowuje danych — każdorazowe `SELECT` z widoku wykonuje zapytanie pod spodem. `MATERIALIZED VIEW` **faktycznie zapisuje** wynik na dysku — odczyt jest szybki, ale dane mogą być nieświeże (trzeba je odświeżać przez `REFRESH MATERIALIZED VIEW`).

**Teoria szerzej — przeczytaj zanim zaczniesz pisać:**

* **Widok to nazwa dla zapytania.** Gdy raz po raz piszesz to samo skomplikowane złączenie z agregacją, możesz je „zamrozić” pod nazwą: `CREATE VIEW v_sprzedaz AS SELECT ...`. Od tej pory `SELECT * FROM v_sprzedaz` zachowuje się jak odpytanie tabeli, choć żadnych nowych danych nie ma — przy każdym użyciu baza po cichu wykonuje schowane zapytanie. To czysta wygoda i porządek, *nie* przyspieszenie.

* **Po co widoki — trzy realne powody.** (1) *Czytelność* — chowasz 40-liniowe zapytanie za jedną nazwą, raportów się nie powiela. (2) *Spójność* — wszyscy liczą „przychód” tak samo, bo definicja jest jedna. (3) *Bezpieczeństwo* — możesz dać komuś dostęp do widoku pokazującego tylko wybrane kolumny/wiersze (np. pracowników bez kolumny `salary`), bez dostępu do surowej tabeli.

* **`VIEW` vs `MATERIALIZED VIEW` — kluczowy kompromis.** Zwykły `VIEW` jest **zawsze świeży** (liczy się na bieżąco), ale **wolny**, jeśli zapytanie pod spodem jest ciężkie i wołasz je często. `MATERIALIZED VIEW` **zapisuje wynik na dysku** — odczyt błyskawiczny, ale dane są „zdjęciem” z momentu ostatniego odświeżenia, więc **mogą być nieaktualne**. Wybór: świeżość kontra szybkość.

* **Kiedy co.** `VIEW` — domyślnie, zwłaszcza gdy dane zmieniają się często, a zapytanie nie jest mordercze. `MATERIALIZED VIEW` — do ciężkich raportów/dashboardów, które wystarczy mieć aktualne np. raz na godzinę/dobę; odświeżasz je wtedy zaplanowanym `REFRESH MATERIALIZED VIEW` (np. nocą).

* **`REFRESH` i wariant `CONCURRENTLY`.** Mat. view nie aktualizuje się sam — trzeba go odświeżyć. Zwykły `REFRESH` blokuje odczyty na czas przeliczania. `REFRESH ... CONCURRENTLY` pozwala czytać stare dane w trakcie odświeżania (wymaga indeksu unikalnego na widoku) — ważne na produkcji, gdzie raport musi być cały czas dostępny.

* **Czy przez widok można pisać (`INSERT`/`UPDATE`)?** Tylko przez **proste** widoki (jedna tabela, bez `JOIN`, `GROUP BY`, `DISTINCT`) — wtedy zmiana „przechodzi” do tabeli pod spodem. Widok z agregacją albo złączeniem jest *tylko do odczytu* — baza nie wie, jak rozpisać `UPDATE` na wiele źródeł. W praktyce widoków używa się głównie do czytania.

### Schemat SQL

Używamy tabel z poprzednich sekcji.

### Zadania do wykonania

1. **Stwórz widok** — pracownicy z nazwą działu:

   ```sql
   CREATE OR REPLACE VIEW v_employees AS
   SELECT
       e.id, e.first_name, e.last_name, e.email, e.salary, e.is_active,
       d.name AS department, d.city
   FROM employees e
   LEFT JOIN departments d ON e.department_id = d.id;
   ```

2. **Użyj widoku jak tabeli**:

   ```sql
   SELECT * FROM v_employees WHERE city = 'Warszawa';

   SELECT department, COUNT(*) FROM v_employees GROUP BY department;
   ```

3. **Widok z agregacją** — podsumowanie sprzedaży per książka:

   ```sql
   CREATE OR REPLACE VIEW v_book_sales AS
   SELECT
       b.id, b.title, b.price, b.in_stock,
       COALESCE(SUM(bo.quantity), 0) AS sold_units,
       COALESCE(SUM(bo.quantity * bo.unit_price), 0) AS revenue,
       COUNT(bo.id) AS order_count
   FROM books b
   LEFT JOIN book_orders bo ON bo.book_id = b.id
       AND bo.status IN ('PAID', 'SHIPPED')
   GROUP BY b.id, b.title, b.price, b.in_stock;
   ```

4. **Zapytania z widoku**:

   ```sql
   SELECT title, revenue FROM v_book_sales ORDER BY revenue DESC LIMIT 5;
   SELECT COUNT(*) FROM v_book_sales WHERE sold_units > 0;
   ```

5. **Usuń widok**:

   ```sql
   DROP VIEW IF EXISTS v_employees;
   ```

6. **`MATERIALIZED VIEW` — szybki raport sprzedażowy**:

   ```sql
   CREATE MATERIALIZED VIEW mv_monthly_sales AS
   SELECT
       DATE_TRUNC('month', ordered_at)::DATE AS month,
       COUNT(*) AS orders_count,
       SUM(quantity * unit_price) AS revenue,
       AVG(quantity * unit_price) AS avg_order_value
   FROM book_orders
   WHERE status IN ('PAID', 'SHIPPED')
   GROUP BY DATE_TRUNC('month', ordered_at);
   ```

7. **Odczyt z mat. view** — szybszy niż liczenie za każdym razem:

   ```sql
   SELECT * FROM mv_monthly_sales ORDER BY month;
   ```

8. **Odświeżenie mat. view** — po nowych danych:

   ```sql
   REFRESH MATERIALIZED VIEW mv_monthly_sales;
   ```

9. **Indeks na mat. view**:

   ```sql
   CREATE INDEX idx_mv_monthly_sales_month ON mv_monthly_sales(month);
   ```

10. **`REFRESH MATERIALIZED VIEW CONCURRENTLY`** — bez blokowania odczytów (wymaga indeksu unikalnego):

    ```sql
    CREATE UNIQUE INDEX idx_mv_monthly_sales_unique ON mv_monthly_sales(month);
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_monthly_sales;
    ```

11. **Usuń mat. view**:

    ```sql
    DROP MATERIALIZED VIEW IF EXISTS mv_monthly_sales;
    ```

12. **Widok z warunkiem dla bezpieczeństwa** — pokazujący tylko aktywnych pracowników (jak pseudo-row-level-security):

    ```sql
    CREATE OR REPLACE VIEW v_active_employees AS
    SELECT id, first_name, last_name, email, salary FROM employees WHERE is_active = TRUE;
    ```

13. **`UPDATE` przez widok** — proste widoki mogą być updatowalne:

    ```sql
    UPDATE v_active_employees SET salary = salary + 100 WHERE id = 5;
    -- Działa, bo widok jest prosty (jedna tabela, bez agregacji)
    ```

### Pytania kontrolne

1. Czym `VIEW` różni się od `MATERIALIZED VIEW`?
2. Czemu dane w `MATERIALIZED VIEW` mogą być nieświeże?
3. Kiedy używać `VIEW`, a kiedy `MATERIALIZED VIEW`?
4. Czy można robić `INSERT`/`UPDATE` przez `VIEW`?
5. Co robi `REFRESH MATERIALIZED VIEW CONCURRENTLY`?
6. Czy `VIEW` z `JOIN` lub `GROUP BY` jest zwykle updatowalny?

---

## 11. Samodzielne zadania — sprawdź sam siebie

**Cel:** Tu nie ma gotowych zapytań. Dostajesz **polecenie** i **oczekiwany wynik**. Twoim zadaniem jest samodzielnie napisać zapytanie, które ten wynik zwróci, i porównać to, co zwróciła baza, z oczekiwanym wynikiem poniżej. Rozwiązań SQL tu nie ma — masz je wymyślić sam, korzystając z teorii i przykładów z sekcji 1–10.

> **Jakich tabel używają zadania:** `employees` i `departments` (z sekcji 4) oraz `publishers`, `authors`, `books`, `book_authors` (z sekcji 1). Tabela `book_orders` **nie jest** tu potrzebna. Kwoty pieniężne pokazujemy z dwoma miejscami po przecinku, tak jak zwraca je `NUMERIC`.

> **⚠ Najpierw zresetuj dane (patrz ramka „Czy dane są gotowe?” niżej)** — inaczej zadania o cenach książek dadzą inne liczby, bo ćwiczenia z sekcji 3 zmieniają ceny w tabeli `books`.

Trudność rośnie: **zadania 1–8 łatwe**, **9–13 średnie**, **14–17 trudniejsze**.

---

> ### Czy dane są gotowe pod te zadania? (przeczytaj zanim zaczniesz)
>
> **Tak — wszystkie potrzebne tabele i wiersze są już w danych startowych.** Nic nie trzeba *dodawać*. Ale uwaga, **niektóre wcześniejsze ćwiczenia zmieniają dane**, więc liczby mogą się nie zgadzać, jeśli je wykonałeś:
>
> | Tabela | Czy ćwiczenia ją psują? | Wpływ na zadania końcowe |
> |--------|--------------------------|--------------------------|
> | `employees`, `departments` | praktycznie nie (sekcja 10, zad. 13 zmienia pensję pracownika `id=5` o +100) | **brak** — żaden oczekiwany wynik nie zależy od tej kwoty |
> | `publishers`, `book_authors` | nie | brak |
> | `authors` | sekcja 3 (zad. 1–2) **dodaje** 4 nowych autorów bez książek | brak — zad. 17 używa `JOIN`, więc autorzy bez książek i tak nie wchodzą |
> | `books` | sekcja 3 (zad. 6, 7) **zmienia ceny** (`id=1` → 209, książki Pearsona +10%) | **ZMIENIA** wyniki zad. 8 i 16 (filtrowanie po cenie) |
>
> **Co zrobić, żeby wyniki się zgadzały:** wczytaj ponownie oryginalne dane z tabeli `books` (i dla porządku `authors`). Najprościej — wykonaj jeszcze raz bloki **„Schemat SQL — sklep książkowy”** oraz **„Dane testowe”** z **sekcji 1** (odtworzą `publishers`, `authors`, `books`, `book_authors` od zera). Jeśli chcesz mieć absolutnie czysty stan wszystkich tabel, przeładuj też **sekcję 4** (`departments`, `employees`). Tabeli `book_orders` przeładowywać nie musisz — te zadania jej nie dotykają.
>
> Krócej: **„dane są — wystarczy odświeżyć `books` (i `authors`) do stanu z sekcji 1, reszta gra”.**

---

### Łatwe

**Zadanie 1.** Policz, ilu jest wszystkich pracowników w tabeli `employees`.

Oczekiwany wynik:

```
count
-----
   12
```

**Zadanie 2.** Wypisz imię i nazwisko pracowników działu Engineering (`department_id = 1`), posortowane po pensji malejąco.

Oczekiwany wynik (4 wiersze):

```
first_name   | last_name
-------------+-----------
Aleksandra   | Pawlak      (16500)
Anna         | Nowak       (15000)
Jan          | Kowalski    (12000)
Maria        | Wiśniewska  (11000)
```

**Zadanie 3.** Wypisz pracowników z pensją **powyżej** 12000 (imię, nazwisko, pensja).

Oczekiwany wynik (4 wiersze): Magdalena Kamińska 18000, Aleksandra Pawlak 16500, Anna Nowak 15000, Tomasz Lewandowski 13500. *(Jan Kowalski z 12000 NIE wchodzi — warunek jest ostry.)*

**Zadanie 4.** Wypisz pracowników, którzy **nie mają** numeru telefonu.

Oczekiwany wynik (3 wiersze): Maria Wiśniewska, Marta Zielińska, Paweł Jankowski.

*Wskazówka: do `NULL` nie używaj `=`. Zastanów się, dlaczego `WHERE phone = NULL` zwróci 0 wierszy.*

**Zadanie 5.** Pokaż najwyższą i najniższą pensję w firmie.

Oczekiwany wynik:

```
max    | min
-------+------
18000  | 7500
```

**Zadanie 6.** Pokaż unikalne miasta, w których znajdują się działy.

Oczekiwany wynik (3 wiersze, kolejność dowolna): Warszawa, Kraków, Wrocław.

**Zadanie 7.** Wypisz pracowników zatrudnionych w **2023** roku (imię, nazwisko, data zatrudnienia).

Oczekiwany wynik (2 wiersze): Paweł Jankowski (2023-01-09), Bartosz Wróbel (2023-03-01).

**Zadanie 8.** Wypisz tytuły książek droższych niż 150 zł, posortowane od najdroższej.

Oczekiwany wynik (5 wierszy): Java in Action (220.00), Effective Java (199.00), Refactoring (189.00), PostgreSQL Cookbook (175.00), The Pragmatic Programmer (169.00).

*Uwaga: ten wynik zakłada oryginalne ceny z sekcji 1 — patrz ramka o resecie danych wyżej.*

### Średnie

**Zadanie 9.** Policz, ilu pracowników jest w każdym dziale. Pokaż `department_id` i liczbę, posortowane po `department_id`. Uwaga na pracownika bez działu.

Oczekiwany wynik:

```
department_id | liczba
--------------+--------
            1 |      4
            2 |      2
            3 |      3
            4 |      1
            5 |      1
       (NULL) |      1
```

**Zadanie 10.** Pokaż średnią pensję w firmie, zaokrągloną do 2 miejsc.

Oczekiwany wynik:

```
srednia
---------
11541.67
```

*(Dla kontroli: suma wszystkich pensji to 138500, podzielona przez 12.)*

**Zadanie 11.** Wypisz pracowników wraz z **nazwą** ich działu (INNER JOIN), ale tylko z działu „Sales”.

Oczekiwany wynik (3 wiersze): Tomasz Lewandowski — Sales, Marta Zielińska — Sales, Bartosz Wróbel — Sales.

**Zadanie 12.** Pokaż działy, które mają **więcej niż 2** pracowników. Wypisz nazwę działu i liczbę pracowników.

Oczekiwany wynik (2 wiersze): Engineering — 4, Sales — 3.

*Wskazówka: warunek na wynik agregacji (`COUNT(*) > 2`) idzie do `HAVING`, nie do `WHERE`.*

**Zadanie 13.** Dla każdej książki pokaż jej tytuł i **nazwę wydawcy**, ale tylko dla wydawców z USA. Posortuj po tytule.

Oczekiwany wynik (6 wierszy): Clean Code — Pearson, Effective Java — Pearson, Java in Action — Manning, PostgreSQL Cookbook — O'Reilly, Refactoring — Pearson, The Pragmatic Programmer — Pearson.

### Trudniejsze

**Zadanie 14.** Pokaż średnią pensję w każdym dziale (nazwa działu + średnia zaokrąglona do 2 miejsc), ale tylko dla działów, w których średnia przekracza 10000. Pomiń pracowników bez działu. Posortuj malejąco po średniej.

Oczekiwany wynik (3 wiersze):

```
dzial        | srednia
-------------+----------
Legal        | 18000.00
Engineering  | 13625.00
Sales        | 10333.33
```

*Wskazówka: warunek na średnią to też `HAVING`. Pracowników bez działu odfiltruj wcześniej (`WHERE`/`JOIN`).*

**Zadanie 15.** Dla każdego pracownika, który **ma** przełożonego, pokaż „pracownik → manager” (imię i nazwisko obu). Użyj self-joina.

Oczekiwany wynik (5 wierszy):

```
pracownik            | manager
---------------------+--------------------
Jan Kowalski         | Anna Nowak
Maria Wiśniewska     | Anna Nowak
Katarzyna Wójcik     | Piotr Lis
Marta Zielińska      | Tomasz Lewandowski
Bartosz Wróbel       | Tomasz Lewandowski
```

*Wskazówka: złącz `employees` z `employees` pod dwoma różnymi aliasami. `INNER JOIN` sam odrzuci pracowników bez managera — z `LEFT JOIN` dostałbyś wszystkich 12.*

**Zadanie 16.** Wypisz tytuły książek droższych niż **średnia cena wszystkich** książek. Użyj podzapytania.

Oczekiwany wynik (6 wierszy, średnia ≈ 146.24): Effective Java, Clean Code, The Pragmatic Programmer, Refactoring, Java in Action, PostgreSQL Cookbook. *(Wiedźmin 39.90 i Solaris 29.00 odpadają.)*

*Uwaga: wynik zakłada oryginalne ceny z sekcji 1 — patrz ramka o resecie danych.*

**Zadanie 17.** Pokaż każdego autora i **liczbę napisanych przez niego książek** (po `book_authors`), posortowane malejąco po liczbie. Który autor napisał więcej niż jedną?

Oczekiwany wynik: na górze **Joshua Bloch — 2** (Effective Java + Java in Action), pozostałych sześciu autorów po 1 (Robert Martin, Andrew Hunt, David Thomas, Andrzej Sapkowski, Stanisław Lem, Martin Fowler).

*Wskazówka: użyj `JOIN` między `authors` i `book_authors` + `GROUP BY` po autorze. Gdybyś dał `LEFT JOIN`, zobaczyłbyś też autorów z 0 książek (jeśli dodałeś ich w sekcji 3).*

---

### Co dalej

Jeśli zrobiłeś wszystkie 17 zadań i Twoje wyniki zgadzają się z oczekiwanymi — masz solidne podstawy SQL: DDL, constraints, DML, `SELECT`, sortowanie, agregacje, `JOIN`-y i podzapytania. Wróć do sekcji 9 (indeksy) i 10 (widoki) i spróbuj zoptymalizować oraz „opakować” swoje zapytania.

---

