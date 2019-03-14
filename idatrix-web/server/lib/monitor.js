
class Monitor{
    constructor(){
        this.route = "monitor";
    }

    adapter(req,res){
        res.send("data");
    }
}

module.exports = Monitor;