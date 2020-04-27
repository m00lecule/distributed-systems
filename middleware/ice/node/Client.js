const Ice = require("ice").Ice;
const Demo = require("./generated/classes").Demo;

const readline = require("readline");

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
});

function categoryQuestion(name) {
    rl.question("INSERT CAT>>\n", (cat) => {
        let wait = false;
        if(car.includes(cat) || t.includes(cat) || vc.includes(cat)) {
            bean(cat,name);
            rl.close();
        }else{
            console.log(`${cat} unknown categhory`);
            console.log(car);
            console.log(vc);
            console.log(t);
        }
    });
}

async function operationOnCart(base) {

    const cart = await Demo.IMovingPrx.checkedCast(base);
    if(cart)
    {

        rl.question("CART ACTION>>\n", (act) => {
            switch (act) {
                case "move":

                case "get":
                    cart.getPosition().then(r => console.log(`${r.x.low} ${r.y.low}`))
                    break;
            }
        });






        console.log("xdddd")

        await cart.getPosition().then(r => console.log(`${r.x.low} ${r.y.low}`))
        // await printer.move(new Demo.Position( new Ice.Long(), new Ice.Long(20,20))).then(r => console.log(r.x));
        // printer.getPosition().then(r => console.log(r));
    }
    else
    {
        console.log("Invalid cart proxy");
    }

}





var recursiveAsyncReadLine = function () {
    rl.question("INSERT NAME>>", (name) => {
        categoryQuestion(name);
    });
};


const vc = ['cap', 'low', 'silly'];
const t = ['heat', 'cool', 'spec'];
const car = ['cart','rcart','mcart'];

const map = {'heater':'heat', "specific heat": "spec", "cart":"cart", "reverse cart": "rcart", "magnifi cart": 'mcart', 'cooler': 'cool',   };

async function bean(cat,name) {
    let communicator;
    try
    {
        communicator = Ice.initialize();
        const baseProxy = (uri) => communicator.stringToProxy(`${uri} :tcp -h localhost -p 10000`);
        const base = baseProxy(`${cat}/${name}`);

        if(car.includes(cat)){
            console.log("before await");
            await operationOnCart(base);
        }else if(t.includes(cat)){

        }else if(vc.includes(cat)){
            bean(cat,name);
        }else{
            console.log(`${cat} unknown categhory`);
            console.log(car);
            console.log(vc);
            console.log(t);
        }
    }
    catch(ex)
    {
        console.log(ex.toString());
        process.exitCode = 1;
    }
    finally
    {
        if(communicator)
        {
            await communicator.destroy();
        }
    }
};

recursiveAsyncReadLine();