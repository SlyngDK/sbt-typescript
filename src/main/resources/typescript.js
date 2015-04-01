/*global process, require */

(function () {
    "use strict";

    var args = process.argv,
        fs = require("fs"),
        mkdirp = require("mkdirp"),
        path = require("path"),
        exec = require("child_process").exec;

    var SOURCE_FILE_MAPPINGS_ARG = 2;
    var TARGET_ARG = 3;
    var OPTIONS_ARG = 4;

    var sourceFileMappings = JSON.parse(args[SOURCE_FILE_MAPPINGS_ARG]);
    var target = args[TARGET_ARG];
    var options = JSON.parse(args[OPTIONS_ARG]);

    var sourcesToProcess = sourceFileMappings.length;
    var results = [];
    var problems = [];

    function compileDone() {
        if (--sourcesToProcess === 0) {
            console.log("\u0010" + JSON.stringify({results: results, problems: problems}));
        }
    }

    function throwIfErr(e) {
        if (e) throw e;
    }

    sourceFileMappings.forEach(function (sourceFileMapping) {

        var input = sourceFileMapping[0];
        var outputFile = sourceFileMapping[1].replace(".ts", ".js");
        var output = path.join(target, outputFile);
        var sourceMapOutput = output + ".map";

        var cmd_args = " ";
        if (options.sourceMap)
            cmd_args += "--sourceMap ";

        if (options.noEmitOnError)
            cmd_args += "--noEmitOnError ";

        var cmd = "tsc";
        if (options.tscPath !== "") {
            cmd = options.tscPath;
        }

        cmd += " " + cmd_args + " --out " + output + " " + input;

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
                        characterOffset: parseInt(stdout.match('\,([0-9])\\)')[1]),
                        lineContent: errLine,
                        source: input
                    });
                } catch (e) {
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