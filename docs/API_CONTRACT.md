# Employee Task Manager - REST API Contract

**Implementation status:** Implemented and verified  
**Verification date:** 24 November 2026

## 1. Endpoint

- Method: `GET`
- Base URL: `https://jsonplaceholder.typicode.com`
- Resource: `/todos`
- Full URL: `https://jsonplaceholder.typicode.com/todos`
- Request header: `Accept: application/json`
- Authentication: No authentication header is configured.
- Connect timeout: 10 seconds
- Read timeout: 10 seconds
- HTTP redirect following: Enabled

The existing endpoint and its response data are used unchanged. No request-side record limit or artificial task data is added.

## 2. Successful Response

- Verified HTTP status: `200 OK`
- Verified record count: `200`
- Response format: JSON array

Representative response shape:

```json
[
  {
    "userId": 1,
    "id": 1,
    "title": "Example task title",
    "completed": false
  }
]
PS C:\Users\HP\AndroidStudioProjects\Employeetaskmanager> $path = ".\docs\API_CONTRACT.md"
PS C:\Users\HP\AndroidStudioProjects\Employeetaskmanager> Copy-Item $path (Join-Path $env:TEMP "API_CONTRACT.before-day7.md.bak") -Force
PS C:\Users\HP\AndroidStudioProjects\Employeetaskmanager> 
PS C:\Users\HP\AndroidStudioProjects\Employeetaskmanager> $content = @'
>> # Employee Task Manager - REST API Contract
>> 
>> **Implementation status:** Implemented and verified  
>> **Verification date:** 24 November 2026
>> 
>> ## 1. Endpoint
>> 
>> - Method: `GET`
>> - Base URL: `https://jsonplaceholder.typicode.com`
>> - Resource: `/todos`
>> - Full URL: `https://jsonplaceholder.typicode.com/todos`
>> - Request header: `Accept: application/json`
>> - Authentication: No authentication header is configured.
>> - Connect timeout: 10 seconds
>> - Read timeout: 10 seconds                                                                                                                                 
>> - HTTP redirect following: Enabled                                                                                                                         
>>                                                                                                                                                            
>> The existing endpoint and its response data are used unchanged. No request-side record limit or artificial task data is added.                             
>>                                                                                                                                                            
>> ## 2. Successful Response                                                                                                                                  
>>                                                                                                                                                            
>> - Verified HTTP status: `200 OK`                                                                                                                           
>> - Verified record count: `200`                                                                                                                             
>> - Response format: JSON array                                                                                                                              
>>                                                                                                                                                            
>> Representative response shape:                                                                                                                             
>>                                                                                                                                                            
>> ```json                                                                                                                                                    
>> [                                                                                                                                                          
>>   {                                                                                                                                                        
>>     "userId": 1,                                                                                                                                           
>>     "id": 1,                                                                                                                                               
>>     "title": "Example task title",                                                                                                                         
>>     "completed": false                                                                                                                                     
>>   }
>> ]
>> ```

### Response fields

| Field | Kotlin type | Description |
|---|---|---|
| userId | Int | API user identifier |
| id | Long | Remote task identifier |
| title | String | Task title |
| completed | Boolean | Completion status |

The response is represented by `ApiTaskDto`. API records are not converted into `TaskEntity`; the API does not provide priority values.

## 3. HTTP and Parsing Behavior

- HTTP 2xx: Read and parse the JSON response.
- HTTP 204: Return a successful empty list.
- Other non-2xx responses: Return `ApiHttpException` containing the status code and up to 500 characters of the error body.
- Blank successful response body: Return a failure.
- Malformed JSON, missing or invalid fields, or blank task title: Return a parsing/validation failure.
- Network and timeout exceptions: Returned as failures.
- Coroutine cancellation: Rethrown rather than converted into a normal failure.
- The HTTP connection is disconnected in the `finally` block.

## 4. Application Data Flow

`TaskDetailsActivity -> TaskViewModel -> TaskRepository -> TaskApiClient -> GET /todos`

The client parses the response into `List<ApiTaskDto>`. The repository returns `Result<List<ApiTaskDto>>`, and the ViewModel publishes the result through `apiTaskState`.

The Activity displays remote data through a separate `ApiTaskAdapter` and API RecyclerView.

## 5. UI States

- `Idle`: Initial state before retrieval.
- `Loading`: API request in progress.
- `Success`: Display the returned API task list and loaded record count.
- `Empty`: Display when the API returns no records.
- `Error`: Display an error message and the Retry action.

Retry triggers another API request. Network recovery and retry were manually verified.

## 6. Local Data Isolation

- Remote API records remain separate from Room records.
- API records are not inserted, updated, or deleted in Room.
- Existing local task creation, completion, deletion, filtering, priority, and dashboard counts remain on the Room data path.
- No artificial priority is assigned to remote tasks.
- No API cache or synchronization mechanism is implemented.

## 7. Verification Evidence

### Unit tests

`TaskApiClientTest`: 5 tests passed, 0 failures, 0 errors, 0 skipped.

Coverage:
1. Successful response, GET method, Accept header, and connection cleanup.
2. HTTP 204 returns an empty list.
3. HTTP 503 returns `ApiHttpException`.
4. Blank response body returns failure.
5. Network failure returns failure.

Existing `ExampleUnitTest`: 1 test passed.

**Combined result:** 6 tests passed, 0 failures, 0 errors, 0 skipped.

### Build and physical-device verification

- `:app:assembleDebug`: BUILD SUCCESSFUL after the API list scrolling correction.
- `:app:test`: BUILD SUCCESSFUL.
- Physical device: `CPH1877`.
- API task count displayed: 200.
- First and last displayed API identifiers verified by user: #1 and #200.
- Remote task title, status, API ID, and user ID displayed.
- Local Room dashboard and task list remained separate.
- Airplane Mode test: API error state and Retry button displayed.
- Recovery test: After Wi-Fi reconnection and Retry, all 200 API tasks loaded again.

## 8. Known Limitations

- Remote API tasks are read-only in this implementation.
- Remote records are not cached in Room.
- No offline synchronization or remote write operations are implemented.
- The API endpoint is a mock/sample data source.
