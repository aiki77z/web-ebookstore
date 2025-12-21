@echo off
REM MapReduce Job Runner Script (Windows)
REM Usage: run-mapreduce.bat [input_dir] [output_dir] [keywords_file]

REM Set default values
set INPUT_DIR=%~1
if "%INPUT_DIR%"=="" set INPUT_DIR=..\hadoop\input

set OUTPUT_DIR=%~2
if "%OUTPUT_DIR%"=="" set OUTPUT_DIR=..\hadoop\output

set KEYWORDS_FILE=%~3
if "%KEYWORDS_FILE%"=="" set KEYWORDS_FILE=..\hadoop\keywords.txt

REM Set Hadoop Home
if "%HADOOP_HOME%"=="" (
    echo ERROR: HADOOP_HOME is not set. Please set it to your Hadoop installation directory.
    exit /b 1
)

REM Check if input directory exists
if not exist "%INPUT_DIR%" (
    echo ERROR: Input directory not found: %INPUT_DIR%
    echo Please run the data export program first.
    exit /b 1
)

REM Check if keywords file exists
if not exist "%KEYWORDS_FILE%" (
    echo WARNING: Keywords file not found: %KEYWORDS_FILE%
    echo Will use default keyword list.
    set KEYWORDS_FILE=
)

REM Clean up old output directory
if exist "%OUTPUT_DIR%" (
    echo Cleaning up old output directory: %OUTPUT_DIR%
    rmdir /s /q "%OUTPUT_DIR%"
)

REM Check if project is compiled
if not exist "target\classes\com\ebookstore\hadoop\KeywordCountJob.class" (
    echo Compiling project...
    call mvn clean compile -q
    if %ERRORLEVEL% NEQ 0 (
        echo ERROR: Compilation failed!
        exit /b 1
    )
)

echo Starting MapReduce Job...
echo Input:  %INPUT_DIR%
echo Output: %OUTPUT_DIR%
if defined KEYWORDS_FILE echo Keywords: %KEYWORDS_FILE%

REM Build Classpath (Including YARN and HDFS libraries to fix NoClassDefFoundError)
set "CP=target\classes"
set "CP=%CP%;%HADOOP_HOME%\share\hadoop\common\*"
set "CP=%CP%;%HADOOP_HOME%\share\hadoop\common\lib\*"
set "CP=%CP%;%HADOOP_HOME%\share\hadoop\mapreduce\*"
set "CP=%CP%;%HADOOP_HOME%\share\hadoop\yarn\*"
set "CP=%CP%;%HADOOP_HOME%\share\hadoop\hdfs\*"

REM Run MapReduce Job
echo Running Job...
if defined KEYWORDS_FILE (
    java -cp "%CP%" com.ebookstore.hadoop.KeywordCountJob "%INPUT_DIR%" "%OUTPUT_DIR%" "%KEYWORDS_FILE%"
) else (
    java -cp "%CP%" com.ebookstore.hadoop.KeywordCountJob "%INPUT_DIR%" "%OUTPUT_DIR%"
)

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Job Completed Successfully!
    echo Results saved in: %OUTPUT_DIR%\part-r-00000
    echo.
    echo --- Statistics Result ---
    if exist "%OUTPUT_DIR%\part-r-00000" (
        type "%OUTPUT_DIR%\part-r-00000"
    ) else (
        echo Could not find result file.
    )
) else (
    echo.
    echo ERROR: MapReduce Job Failed!
    exit /b 1
)
