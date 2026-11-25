# Employee Task Manager — Codex Review

## Day 3 Plan Review

### Missing Cases
- Task data fields are defined in the approved task model.
- Storage technology is selected as Room.
- Duplicate-task handling follows the existing insert behavior.
- Empty-task UI is handled in the Task List screen.
- The existing Home -> Task Details navigation is preserved.

### Risks
- Changing existing screens too early could break working Day 1 and Day 2 functionality.
- Task status could become inconsistent if completion state is not managed through a single source of truth.
- Task operations must remain within the approved Screen -> ViewModel -> Repository -> Storage architecture.

## Day 4 Scope Review

### Required scope
- Task List screen UI only.
- Task viewing, task creation, completion marking, and Pending/Completed status from the approved Day 3 plan.
- Empty-task state handling.
- Build, launch, inspect runtime behavior, and debug the two controlled bugs.
- Deliver APK, feature branch, PR, Codex review, `DEBUGGING_LOG.md`, and `CURRENT_STATUS.md`.

### Excluded from this PR
- Task priority and priority-based ordering.
- Task deletion and confirmation dialog.
- Dashboard statistics.
- Database schema upgrades or migration tests.
- CI workflow additions.
- Home Screen redesign.

### Source Inspection
- `MainActivity` retains the existing Home -> Task Details click listener.
- `TaskDetailsActivity` wires the Add Task button, submits tasks through `TaskViewModel`, collects the task Flow, and switches between list and empty state.
- The empty-state container is a `LinearLayout`; the Activity retrieves it as `View` to avoid an incorrect view-type cast.
- `TaskViewModel` uses `SharingStarted.Eagerly` so the Room-backed Flow is collected for the ViewModel lifetime.
- Task data model, DAO, Repository, and Room schema remain unchanged from the approved Day 3 baseline.

### Review Findings / Remaining Validation

- Static scope and source-code inspection: Completed.
- Clean Debug APK build after scope cleanup: Successful.
- Unit tests using `testDebugUnitTest`: Successful.
- Debug APK installation: Successful on device `CPH1877 - 10`.
- User confirmed that the app is working after installation.
- Bug A source-level root cause was identified: the empty-state `LinearLayout` was previously retrieved as a `TextView`. The Activity now retrieves it as `View`. Breakpoint confirmation and a specific runtime retest remain pending.
- Bug B root cause is not yet proven by a breakpoint trace. The Flow collection change is retained as a correction attempt, not a confirmed root-cause fix.
- Terminal verification screenshots and controlled-bug debugging evidence remain to be captured.

### Review Outcome

**Static scope/code inspection:** Completed.

**Post-cleanup build:** Passed.

**Unit-test run:** Passed.

**Device installation:** Passed.

**App working check:** Confirmed by the user.

**Controlled Bug A/B debugging proof:** Pending. Do not claim both controlled bugs have been fully reproduced and verified.

---

## Week 2 Day 7 — Independent REST API Review

**Review date:** 24 November 2026  
**Reviewer:** Codex  
**Review type:** Independent, read-only source and documentation review

### Scope

Reviewed the REST API client, DTO, Repository, ViewModel, Activity, adapter, layouts, Manifest, unit tests, API contract, status documentation, decisions, and updated diagrams.

### Review Outcome

**APPROVE WITH MINOR FOLLOW-UP**

The REST API retrieval implementation, HTTP handling, JSON parsing, Repository and ViewModel integration, UI states, Retry action, and independent API RecyclerView are present.

### Critical Findings

None identified.

### Major Findings

None identified.

### Minor Findings

1. **API test coverage**
   - Dedicated tests are missing for malformed JSON, missing or wrong-typed fields, blank titles, HTTP 4xx, timeout, redirect handling, and cancellation propagation.
   - ViewModel error-state transitions and Retry guard also lack dedicated tests.

2. **Error message handling**
   - `TaskApiClient.kt`:44 and `TaskViewModel.kt`:70–76.
   - Raw server error text may be exposed through the UI.
   - Recommendation: use safe user-facing messages and retain diagnostic details separately.

3. **Verification documentation consistency**
   - `API_CONTRACT.md` records test, build, device, and network verification.
   - `CURRENT_STATUS.md` should explicitly identify API_CONTRACT.md as the detailed verification record and distinguish previously recorded results from newly captured Day 7 evidence.

### Missing Test Coverage

- JSON parsing and field validation.
- Malformed JSON and non-object array elements.
- HTTP 4xx responses.
- Timeout and redirect behavior.
- Coroutine cancellation propagation.
- ViewModel state transitions and Retry guard.

### Confirmed Bugs Versus Possible Risks

- Confirmed bugs: None established by this review.
- Possible risk: Server error text may appear in the UI.
- Possible risk: Parser and ViewModel edge cases have limited automated test coverage.

### Positive Design Findings

- API tasks remain separate from Room records.
- Remote records are not converted into `TaskEntity`.
- Retry uses `loadApiTasks(forceRefresh = true)`.
- Existing Room task storage and dashboard remain independent.

### Review Limitations

- This was a read-only review.
- No source or documentation files were modified by Codex.
- No tests or builds were independently executed during this review.

### Final Recommendation

**APPROVE WITH MINOR FOLLOW-UP**

The Week 2 Day 7 REST API retrieval scope is approved with the minor improvements documented above.
