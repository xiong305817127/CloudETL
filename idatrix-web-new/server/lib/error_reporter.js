const fs = require("fs")
const path = require("path")

/**
 * 前端bug集合
 */
class ErrorReporter{
    constructor(){
        this.route = "error_reporter";
        this.checkDir = this.checkDir.bind(this);
        this.adapter = this.adapter.bind(this);
    }

    adapter(req,res){
        res.send();
        console.log(req.query,this)

        for(let i in req.query){
            const fileName = path.join(__dirname,"../logs/" + i + ".log");

            this.checkDir(1,2)
            fs.open(fileName, "a+" , (err, fd) => {

                const time = new Date();
                const error = `
${req.query[i]}
${time}
                `

                if (err) {
                  if (err.code === 'EEXIST') {
                    fs.write(fd, error, 0 );
                    return;
                  }
                }else{
                    fs.write(fd, error, 0 );
                    return;
                }
            });
        }
    }

    checkDir(fileName,next){

        let logPath = path.join(__dirname,"../logs/");
        fs.readdir(logPath,(err,files)=>{
            if(err){
                throw new Error(err.message);
            }
            
            
        })
    }

    getNewName(files,file){
        if(files.length === 0){
            return file + "-0.log";
        }else{
            let fileCount = 0;
            for(let i in files){
                if(files[i] === file + "-" + fileCount + ".log"){
                    return files[i];
                }
            }
        }

        return file + "-0.log";
    }
}

module.exports = ErrorReporter;