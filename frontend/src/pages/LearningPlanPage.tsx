import { useEffect, useState } from 'react'
import {
  getLearningPlan,
  recalculateLearningPlan,
} from '../services/api'

type UrgencyLevel = 'RED' | 'YELLOW' | 'GREEN'

interface PlanTaskDTO {
  taskId: string
  title: string
  recommendedMinutes: number
  urgency: UrgencyLevel
}

interface WeekEntryDTO {
  date: string
  tasks: PlanTaskDTO[]
}

interface LearningPlanDTO {
  generatedAt: string
  weekEntries: WeekEntryDTO[]
}

const weekdays = [
  'Montag',
  'Dienstag',
  'Mittwoch',
  'Donnerstag',
  'Freitag',
  'Samstag',
  'Sonntag',
]

function getMonday(date: Date): Date {
  const result = new Date(
    date.getFullYear(),
    date.getMonth(),
    date.getDate(),
  )

  const day = result.getDay()
  const difference = day === 0 ? -6 : 1 - day

  result.setDate(result.getDate() + difference)

  return result
}

function addDays(date: Date, days: number): Date {
  const result = new Date(
    date.getFullYear(),
    date.getMonth(),
    date.getDate(),
  )

  result.setDate(result.getDate() + days)

  return result
}

function addWeeks(date: Date, weeks: number): Date {
  return addDays(date, weeks * 7)
}

function formatDate(date: Date): string {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  return `${year}-${month}-${day}`
}

function formatDisplayDate(date: string): string {
  const [year, month, day] = date
    .split('-')
    .map(Number)

  return new Intl.DateTimeFormat('de-DE').format(
    new Date(year, month - 1, day),
  )
}

function getCalendarWeek(date: Date): number {
  const target = new Date(
    date.getFullYear(),
    date.getMonth(),
    date.getDate(),
  )

  const dayNumber =
    target.getDay() === 0 ? 7 : target.getDay()

  target.setDate(
    target.getDate() + 4 - dayNumber,
  )

  const yearStart = new Date(
    target.getFullYear(),
    0,
    1,
  )

  return Math.ceil(
    ((target.getTime() - yearStart.getTime()) /
      86400000 +
      1) /
      7,
  )
}

function getUrgencyStyles(
  urgency: UrgencyLevel,
): {
  backgroundColor: string
  borderColor: string
} {
  if (urgency === 'RED') {
    return {
      backgroundColor: '#fee2e2',
      borderColor: '#dc2626',
    }
  }

  if (urgency === 'YELLOW') {
    return {
      backgroundColor: '#fef3c7',
      borderColor: '#d97706',
    }
  }

  return {
    backgroundColor: '#dcfce7',
    borderColor: '#16a34a',
  }
}

function getUrgencyLabel(
  urgency: UrgencyLevel,
): string {
  if (urgency === 'RED') {
    return 'Dringend'
  }

  if (urgency === 'YELLOW') {
    return 'Mittel'
  }

  return 'Nicht dringend'
}

export default function LearningPlanPage() {
  const [plan, setPlan] =
    useState<LearningPlanDTO | null>(null)

  const [selectedMonday, setSelectedMonday] =
    useState<Date>(() => getMonday(new Date()))

  const [loading, setLoading] = useState(true)
  const [recalculating, setRecalculating] =
    useState(false)

  const [error, setError] = useState('')

  useEffect(() => {
    void loadPlan()
  }, [])

  async function loadPlan() {
    setLoading(true)
    setError('')

    try {
      const data = await getLearningPlan()
      setPlan(data)
    } catch (loadError) {
      setError(
        loadError instanceof Error
          ? loadError.message
          : 'Lernplan konnte nicht geladen werden.',
      )
    } finally {
      setLoading(false)
    }
  }

  async function handleRecalculate() {
    setRecalculating(true)
    setError('')

    try {
      const data =
        await recalculateLearningPlan()

      setPlan(data)
    } catch (recalculateError) {
      setError(
        recalculateError instanceof Error
          ? recalculateError.message
          : 'Lernplan konnte nicht neu berechnet werden.',
      )
    } finally {
      setRecalculating(false)
    }
  }

  const selectedWeekStart =
    formatDate(selectedMonday)

  const selectedWeekEntries =
    plan?.weekEntries.filter((entry) => {
      const [year, month, day] =
        entry.date.split('-').map(Number)

      const entryDate = new Date(
        year,
        month - 1,
        day,
      )

      return (
        formatDate(getMonday(entryDate)) ===
        selectedWeekStart
      )
    }) ?? []

  const weekEntries = Array.from(
    { length: 7 },
    (_, index) => {
      const date = addDays(
        selectedMonday,
        index,
      )

      const dateString = formatDate(date)

      return (
        selectedWeekEntries.find(
          (entry) =>
            entry.date === dateString,
        ) ?? {
          date: dateString,
          tasks: [],
        }
      )
    },
  )

  const calendarWeek =
    getCalendarWeek(selectedMonday)

  return (
    <main className="page">
      <div className="page-header">
        <div>
          <h1>Lernplan</h1>

          <p>
            Deine empfohlene Lernplanung für die
            aktuelle Woche.
          </p>
        </div>

        <button
          type="button"
          onClick={() =>
            void handleRecalculate()
          }
          disabled={recalculating}
        >
          {recalculating
            ? 'Wird berechnet...'
            : 'Plan neu berechnen'}
        </button>
      </div>

      {error && (
        <p className="error-message">
          {error}
        </p>
      )}

      {loading ? (
        <p>Lernplan wird geladen...</p>
      ) : (
        <section className="tasks-section">
          <div
            className="section-header"
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              gap: '16px',
              marginBottom: '20px',
            }}
          >
            <h2>Wochenplan</h2>

            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '10px',
              }}
            >
              <button
                type="button"
                onClick={() =>
                  setSelectedMonday(
                    (current) =>
                      addWeeks(current, -1),
                  )
                }
              >
                ← Zurück
              </button>

              <strong>
                KW {calendarWeek}
              </strong>

              <button
                type="button"
                onClick={() =>
                  setSelectedMonday(
                    (current) =>
                      addWeeks(current, 1),
                  )
                }
              >
                Weiter →
              </button>
            </div>
          </div>

          <div
            style={{
              display: 'grid',
              gridTemplateColumns:
                'repeat(7, minmax(130px, 1fr))',
              gap: '10px',
              width: '100%',
              overflowX: 'auto',
            }}
          >
            {weekEntries.map(
              (entry, index) => (
                <article
                  key={entry.date}
                  style={{
                    minHeight: '180px',
                    padding: '12px',
                    border: '1px solid #d1d5db',
                    borderRadius: '10px',
                    backgroundColor: '#ffffff',
                    boxSizing: 'border-box',
                  }}
                >
                  <h3
                    style={{
                      margin: '0 0 5px',
                      fontSize: '16px',
                    }}
                  >
                    {weekdays[index]}
                  </h3>

                  <p
                    style={{
                      margin: '0 0 12px',
                      fontSize: '13px',
                      color: '#6b7280',
                    }}
                  >
                    {formatDisplayDate(
                      entry.date,
                    )}
                  </p>

                  {entry.tasks.length === 0 ? (
                    <p
                      style={{
                        fontSize: '13px',
                        color: '#6b7280',
                      }}
                    >
                      Keine Aufgaben
                    </p>
                  ) : (
                    <div
                      style={{
                        display: 'flex',
                        flexDirection: 'column',
                        gap: '8px',
                      }}
                    >
                      {entry.tasks.map(
                        (task) => {
                          const urgencyStyles =
                            getUrgencyStyles(
                              task.urgency,
                            )

                          return (
                            <div
                              key={`${entry.date}-${task.taskId}`}
                              style={{
                                padding: '8px',
                                borderLeft:
                                  `4px solid ${urgencyStyles.borderColor}`,
                                borderRadius:
                                  '6px',
                                backgroundColor:
                                  urgencyStyles.backgroundColor,
                                fontSize: '12px',
                              }}
                            >
                              <div
                                style={{
                                  fontWeight: 600,
                                  marginBottom:
                                    '4px',
                                }}
                              >
                                {task.title}
                              </div>

                              <div>
                                {
                                  task.recommendedMinutes
                                }{' '}
                                Minuten
                              </div>

                              <div
                                style={{
                                  marginTop:
                                    '2px',
                                  fontWeight: 500,
                                }}
                              >
                                {getUrgencyLabel(
                                  task.urgency,
                                )}
                              </div>
                            </div>
                          )
                        },
                      )}
                    </div>
                  )}
                </article>
              ),
            )}
          </div>
        </section>
      )}
    </main>
  )
}