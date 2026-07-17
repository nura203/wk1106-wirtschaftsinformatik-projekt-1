# B2 — Batch

## Anwendbarkeit

**Nicht anwendbar.**

Der Study Planer hat keine Batch-Verarbeitung im Sinne von Siedersleben. Es gibt keine geplanten Massenverarbeitungen von Nutzerdaten, keine Offline-Verarbeitung und keine zeitgesteuerten Bulk-Operationen.

**Hintergrundjob (kein Batch):** Der Erinnerungs-Scheduler (AF-10) läuft täglich um 08:00 Uhr als Spring `@Scheduled`-Task. Er prüft aktive `REMINDER`-Einträge und löst einzelne Benachrichtigungen aus. Da er keine Massendaten verarbeitet und kein eigenständiges Eingabe-/Ausgabeverfahren hat, gilt er nicht als Batch — er ist in F3 (AF-10) und N2 dokumentiert.
