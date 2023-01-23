# Sudoku

## SudokuApplication

Klasa do obsługi biblioteki JavaFX

## SudokuController

Klasa do obsługi biblioteki JavaFX - zawiera obsługę wszystkich zdarzeń i cała logikę gry Sudoku

## SudokuGenerator

Klasa do generowania i rozwiązywania plansz Sudoku. Do obu tych operacji stosuje algorytm typu backtracker. Jeśli dojdzie do pola, na którym nie można wstawić żadnej cyfry, cofa się i wybiera inną cyfrę na poprzednim.
* Do generowania na każdym polu losuje losową liczbę. Po zakończeniu usuwa określoną liczbę losowych pól według trudności
* Do rozwiązywania na każdym pustym polu wstawia kolejną dozwoloną cyfrę. Omija wypełnione pola.

## SudokuSaveLoad

Klasa odpowiadająca za zapis i ładowanie plansz. Znajdują się w niej metody przerabiajace tablice różnych typów na tekst i odwrotnie
* Żeby zapisać uruchamiany jest proces oddzielnej aplikacji ze względu na konflikt bibliotek PDFBox i JavaFX

## IntList

Klasa dziedzicząca po ArrayLiście liczb całkowitych. Dzięki temu, że nie jest generyczna można utworzyć jej tablice