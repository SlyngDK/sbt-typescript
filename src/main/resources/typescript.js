/*global process, require */

(function () {
    "use strict";

    var args = process.argv,
    //fs = require("fs"),
        fs = require("fs"),
        mkdirp = require("mkdirp"),
        path = require("path"),
        exec = require("child_process").exec,
        osSep = process.platform === 'win32' ? '\\' : '/';

    var SOURCE_FILE_MAPPINGS_ARG = 2;
    var TARGET_ARG = 3;
    var OPTIONS_ARG = 4;

    var sourceFileMappings = JSON.parse(args[SOURCE_FILE_MAPPINGS_ARG]);
    var target = args[TARGET_ARG];
    var options = JSON.parse(args[OPTIONS_ARG]);
    var sourceRoot = sourceFileMappings[0][0].slice(0, sourceFileMappings[0][1].length * -1);

    var results = [];
    var problems = [];

    var logFile = target + "/compile.log";

    function log(text) {
        fs.appendFile(logFile, text + "\n", function (err) {
            if (err) {
                console.log("Error appending to log file '" + logFile + "': " + err);
            }
        });
    }

    function log_debug(text) {
        if (options.debug) {
            console.log(text);
        }
        if (typeof text === 'string' || text instanceof String)
            log(text);
        else
            log(JSON.stringify(text))
    }

    try {
        if (fs.existsSync(logFile))
            fs.unlinkSync(logFile);
    } catch (e) {
    }

    function compileDone() {
        log_debug({results: results, problems: problems})
        // log(JSON.stringify({results: results, problems: problems}));
        console.log("\u0010" + JSON.stringify({results: results, problems: problems}));
    }

    function throwIfErr(e) {
        if (e) throw e;
    }

    function mkdirSync_p(path, mode, position) {
        var parts = require('path').normalize(path).split(osSep);

        mode = mode || process.umask();
        position = position || 0;

        if (position >= parts.length) {
            return true;
        }

        var directory = parts.slice(0, position + 1).join(osSep) || osSep;
        try {
            fs.statSync(directory);
            mkdirSync_p(path, mode, position + 1);
        } catch (e) {
            try {
                fs.mkdirSync(directory, mode);
                mkdirSync_p(path, mode, position + 1);
            } catch (e) {
                if (e.code != 'EEXIST') {
                    throw e;
                }
                mkdirSync_p(path, mode, position + 1);
            }
        }
    }

    if (!fs.existsSync(target)) {
        mkdirSync_p(target, '0755');
    }
    sourceFileMappings.forEach(function (sourceFileMapping) {
        var input = sourceFileMapping[0];
        var outputFile = sourceFileMapping[1].replace(".ts", ".js");
        var output = path.join(target, outputFile);
        var outputDir = path.dirname(path.join(target, outputFile));
        var sourceMapOutput = output + ".map";

        log_debug({
            input: input
            , outputFile: outputFile
            , outputDir: outputDir
            , output: output
            , sourceMapOutput: sourceMapOutput
        });


        var cmd = "tsc";
        if (options.tscPath !== "") {
            cmd = options.tscPath;
        }

        cmd += " --module " + options.module;
        cmd += " --target " + options.target;
        cmd += " --moduleResolution " + options.moduleResolution;
        if (options.experimentalDecorators) {
            cmd += " --experimentalDecorators";
        }
        if (options.sourceMap) {
            cmd += " --sourceMap";
        }
        if (options.emitDecoratorMetadata) {
            cmd += " --emitDecoratorMetadata";
        }
        if (options.noEmitOnError) {
            cmd += " --noEmitOnError";
        }

        cmd += " --outDir " + target + " --rootDir " + sourceRoot + " " + input;

        log_debug("Command:" + cmd);

        exec(cmd, function (error, stdout, stderr) {
            if (error == null) {
                results.push({
                    source: input,
                    result: {
                        filesRead: [input],
                        filesWritten: options.sourceMap ? [output, sourceMapOutput] : [output]
                    }
                });
                compileDone();
            } else {
                try {
                    log("Error:" + error);
                    log("Stdout:" + stdout);
                    log("Stderr:" + stderr);

                    // Start Hack ignore errors on compile in node_modules/
                    var errorOnlyInNodeModules = true;
                    stdout.split('\n').forEach(function (line) {
                        if (line.lastIndexOf("node_modules", 0) === 0) {

                        } else {
                            if (line !== "")
                                errorOnlyInNodeModules = false;
                        }
                    });
                    if (errorOnlyInNodeModules) {
                        results.push({
                            source: input,
                            result: {
                                filesRead: [input],
                                filesWritten: options.sourceMap ? [output, sourceMapOutput] : [output]
                            }
                        });
                        compileDone();
                        return;
                    }
                    // End Hack

                    var errLineNum = parseInt(stdout.match('([0-9])\,')[1]);
                    var errLine;

                    var lineNum = 1;
                    fs.readFileSync(input).toString().split('\n').forEach(function (line) {
                        if (lineNum == errLineNum) {
                            errLine = line;
                        }
                        lineNum++;
                    });
                    problems.push({
                        message: stdout,
                        severity: "error",
                        lineNumber: errLineNum,
                        characterOffset: parseInt(stdout.match('\,([0-9]+)\\)')[1]),
                        lineContent: errLine,
                        source: input
                    });
                } catch (e) {
                    log(e);
                    problems.push({
                        message: stderr,
                        severity: "error"
                    });
                }
                results.push({
                    source: input,
                    result: null
                });

                compileDone();
            }
        });
    });
}());