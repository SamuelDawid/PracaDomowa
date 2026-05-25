import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Audit {
    String operation();

    boolean critical() default false;
}

/*
Zwróćcie uwagę na kilka elementów:

@Documented – powoduje, że adnotacja będzie widoczna w Javadocu.
@Retention(RetentionPolicy.RUNTIME) – adnotacja jest dostępna w runtime, możemy ją odczytać refleksją.
@Target(ElementType.METHOD) – adnotacji można używać tylko na metodach.
Parametry operation i critical zachowują się jak pola konfiguracyjne adnotacji.
 */