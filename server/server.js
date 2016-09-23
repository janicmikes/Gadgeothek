var express = require('express');
var http = require('http');
var WebSocket = require('ws');
var WebSocketServer = require('ws').Server;
var port = 8080;

var allowCrossDomain = function(req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
    res.header('Access-Control-Allow-Headers', 'Content-Type');
    next();
};

var app = express();
app.use(allowCrossDomain);
app.use(express.bodyParser());
app.use(express.cookieParser());
app.use(express.session({
    secret: '2234567890QWERTY'
}));
app.use(app.router);

var server = http.createServer(app)
server.listen(port)

var wss = new WebSocketServer({
    server: server
});

function createGuid() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0,
            v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

function getSecurityTokenFromHeader(req) {
	// first try to determine the token from the header
	// added 2015-11-10 to support .NET (which prohibits POST body in GET request)		
	var token = { 
		securityToken: req.headers["x-security-token"],
		customerId: req.headers["x-customer-id"]	
	}
	return token;
}

function getSecurityToken(req) {
	
	var token = getSecurityTokenFromHeader(req);
	
	if (token.securityToken) {
		//console.log("got auth values from header...");
	}
	else if (req.body) {
		// now try the message body:
		token = JSON.parse(req.body.token);

		if (token.securityToken) {
			//console.log("got auth values from body...");	
		}		
	}
	
	return token;
}


function checkAuth(req, res, next) {
	console.log("checking auth...");

	//console.log(req.headers);
	//console.log(tokens);

	var token = getSecurityToken(req);

    if (tokens[token.customerId].securityToken == token.securityToken) {
		console.log("Ok: authorization complete.")
        next();
    } else {
		console.log("Error: authorization failed.")
        res.send('You are not authorized!');
    }
}


wss.broadcast = function(data) {
    for (var i in this.clients)
        this.clients[i].send(data);
};

var maxReservations = 3;

var customers = [];
var gadgets = [];
var loans = [];
var reservations = [];
var tokens = {};

customers.push({
    name: 'Michael',
    password: "12345",
    email: "m@hsr.ch",
    studentnumber: "10"
});
customers.push({
    name: 'Sepp',
    password: "12345",
    email: "s@hsr.ch",
    studentnumber: "11"
});
customers.push({
    name: 'Bob',
    password: "12345",
    email: "b@hsr.ch",
    studentnumber: "12"
});
customers.push({
    name: 'Iris',
    password: "12345",
    email: "i@hsr.ch",
    studentnumber: "13"
});

gadgets.push({
    name: 'IPhone',
    manufacturer: "apple",
    price: 500,
    inventoryNumber: "20",
    condition: "NEW"
});
gadgets.push({
    name: 'IPhone1',
    manufacturer: "apple",
    price: 500,
    inventoryNumber: "21",
    condition: "NEW"
});
gadgets.push({
    name: 'IPhone2',
    manufacturer: "apple",
    price: 500,
    inventoryNumber: "22",
    condition: "NEW"
});
gadgets.push({
    name: 'IPhone3',
    manufacturer: "apple",
    price: 500,
    inventoryNumber: "23",
    condition: "NEW"
});
gadgets.push({
    name: 'IPhone4',
    manufacturer: "apple",
    price: 500,
    inventoryNumber: "24",
    condition: "NEW"
});
gadgets.push({
    name: 'Android1',
    manufacturer: "Samsung",
    price: 500,
    inventoryNumber: "25",
    condition: "NEW"
});
gadgets.push({
    name: 'Android2',
    manufacturer: "Samsung",
    price: 500,
    inventoryNumber: "26",
    condition: "NEW"
});
loans.push({
    id: "40",
    gadgetId: '20',
    customerId: "10",
    pickupDate: new Date().toJSON(),
    returnDate: null
});
loans.push({
    id: "41",
    gadgetId: '21',
    customerId: "10",
    pickupDate: "2014-08-10T06:10:37.032Z",
    returnDate: null
});


reservations.push({
    id: "80",
    gadgetId: '20',
    customerId: "10",
    reservationDate: "2014-08-22T06:10:37.032Z",
    finished: false
});
reservations.push({
    id: "81",
    gadgetId: '21',
    customerId: "11",
    reservationDate: "2014-08-22T06:10:37.032Z",
    finished: false
});


function addCustomer(customerJson) {
	console.log("addCustomer");
    if (findCustomerByEmail(customerJson.email)) {
        return false;
    }
    customers.push(customerJson);
    wss.broadcast(JSON.stringify({
        target: 'customer',
        type: 'add',
        data: JSON.stringify(customerJson)
    }));
    return true;
}

function addReservation(reservationJson) {
    console.log("addReservation");
    if (filterReservationsPerCustomer(reservationJson.customerId).length >= maxReservations) {
        return false;
    }

    if (!isReservedBy(reservationJson.customerId, reservationJson.gadgetId)) {
        reservations.push(reservationJson);
        wss.broadcast(JSON.stringify({
            target: 'reservation',
            type: 'add',
            data: JSON.stringify(reservationJson)
        }));
        return true;
    }
    return false;
}

app.post('/customers', function(req, res) {
    console.log('/customers: POST');
    var customerJson = JSON.parse(req.body.value);
    customers.push(customerJson);
    wss.broadcast(JSON.stringify({
        target: 'customer',
        type: 'add',
        data: JSON.stringify(customerJson)
    }));
    res.json(true);
});

app.get('/customers', function(req, res) {
	console.log('/customers' + ': GET');
    res.json(customers);
})

app.get('/customers/:id', function(req, res) {
	console.log('/customers/' + req.params.id + ': GET');
	var item = findCustomer(req.params.id);
	return res.json(item);
});

app.post('/customers/:id', function(req, res) {
    console.log('/customers/' + req.params.id + ': POST');
    var customerJson = JSON.parse(req.body.value);
    var item = findCustomer(req.params.id);
    if (item != null) {
		console.log('found customer, updating...');	
        merge(customerJson, item);
        wss.broadcast(JSON.stringify({
            target: 'customer',
            type: 'update',
            data: JSON.stringify(item)
        }));
        res.json(item);
    } else {
		console.log('customer NOT FOUND...');	
        res.json(false);
    }
});

app.del('/customers/:id', function(req, res) {
    console.log('/customers/' + req.params.id + ': DELETE');	
	var item = findCustomer(req.params.id);
    if (item) {
		//console.log('found customer, removing...');	
		removeFromArray(customers, item);
        wss.broadcast(JSON.stringify({
            target: 'customer',
            type: 'delete',
            data: JSON.stringify(item)
        }));
        res.json(true);
    } else {
		//console.log('customer NOT FOUND...');	
        res.json(false);
    }
});



function filterLoansPerCustomer(id) {
    return loans.filter(function(ele) {
        return ele.customerId == id && ele.returnDate == null;
    });
}

function filterReservationsPerCustomer(id) {
    return reservations.filter(function(ele) {
        return ele.customerId == id && !ele.finished;
    });
}

function sortReservationArrayPerDate(a, b) {
    return new Date(a.pickupDate).getTime() - new Date(b.pickupDate).getTime();
}

function getWaitingPositionOfReservation(reservation) {
    var sortedReservation = reservations.filter(function(ele) {
        return ele.gadgetId == reservation.gadgetId && !ele.finished;
    }).sort(sortReservationArrayPerDate);
    var realReservation = getReservationBy(reservation.customerId, reservation.gadgetId);

    return sortedReservation.indexOf(realReservation);
}


/*
  ADMIN API (low level access to gadget/loan/reservation databases
*/


app.post('/gadgets', function(req, res) {
    console.log('/gadgets: POST');
	console.log(req.body.value);
	
    var gadgetsJson = JSON.parse(req.body.value);
    gadgets.push(gadgetsJson);
    wss.broadcast(JSON.stringify({
        target: 'gadget',
        type: 'add',
        data: JSON.stringify(gadgetsJson)
    }));

    res.json(true);
});



app.get('/gadgets', function(req, res) {
	console.log('/gadgets' + ': GET');
    res.json(gadgets);
});

app.get('/gadgets/:id', function(req, res) {
	console.log('/gadgets/' + req.params.id + ': GET');
	var item = findGadget(req.params.id);
	return res.json(item);
});


app.post('/gadgets/:id', function(req, res) {
    console.log('/gadgets/' + req.params.id + ': POST');
    var gadgetJson = JSON.parse(req.body.value);
    var item = findGadget(req.params.id);
    if (item != null) {
		//console.log('found gadget, updating...');	
        merge(gadgetJson, item);
        wss.broadcast(JSON.stringify({
            target: 'gadget',
            type: 'update',
            data: JSON.stringify(item)
        }));
        res.json(item);
    } else {
		//console.log('gadget NOT FOUND...');	
        res.json(false);
    }
});


app.del('/gadgets/:id', function(req, res) {
    console.log('/gadgets/' + req.params.id + ': DELETE');	
	var item = findGadget(req.params.id);
    if (item) {
		//console.log('found gadget, removing...');	
		removeFromArray(gadgets, item);
        wss.broadcast(JSON.stringify({
            target: 'gadget',
            type: 'delete',
            data: JSON.stringify(item)
        }));
        res.json(true);
    } else {
		//console.log('gadget NOT FOUND...');	
        res.json(false);
    }
});



app.post('/loans', function(req, res) {
    console.log('/loans' + ': POST');
    var loanJson = JSON.parse(req.body.value);
    loans.push(loanJson);
    wss.broadcast(JSON.stringify({
        target: 'loan',
        type: 'add',
        data: JSON.stringify(loanJson)
    }));

    res.json(true);
});


app.get('/loans', function(req, res) {
	console.log('/loans' + ': GET');
    res.json(loans);
});

app.get('/loans/:id', function(req, res) {
	console.log('/loans/' + req.params.id + ': GET');
	var item = findLoan(req.params.id);
	return res.json(item);
});

app.post('/loans/:id', function(req, res) {
    console.log('/loans/' + req.params.id + ': POST');
    var loanJson = JSON.parse(req.body.value);
    var item = findLoan(req.params.id);	
    if (item != null) {
		//console.log('found loan, updating...');
        merge(loanJson, item);
        wss.broadcast(JSON.stringify({
            target: 'loan',
            type: 'update',
            data: JSON.stringify(item)
        }));
        res.json(item);
		
		console.log(loans);
    } else {
		//console.log('loan NOT FOUND...');
        res.json(false);
    }
});

app.del('/loans/:id', function(req, res) {
    console.log('/loans/' + req.params.id + ': DELETE');	
	var item = findLoan(req.params.id);
    if (item) {
		//console.log('found loan, removing...');	
		removeFromArray(loans, item);
        wss.broadcast(JSON.stringify({
            target: 'loan',
            type: 'delete',
            data: JSON.stringify(item)
        }));
        res.json(true);
    } else {
		//console.log('loan NOT FOUND...');	
        res.json(false);
    }
});



app.post('/reservations', function(req, res) {
    console.log('/reservations' + ': POST');
    var reservationJson = JSON.parse(req.body.value);
    res.json(addReservation(reservationJson));
});


app.get('/reservations', function(req, res) {
	console.log('/reservations' + ': GET');	
    res.json(reservations);
});


app.get('/reservations/:id', function(req, res) {
	console.log('/reservations/' + req.params.id + ': GET');
	var item = findReservation(req.params.id);
	return res.json(item);
});

app.post('/reservations/:id', function(req, res) {
    console.log('/reservations/' + req.params.id + ': POST');
    var reservationJson = JSON.parse(req.body.value);
    var item = findReservation(req.params.id);

    if (item != null) {
        merge(reservationJson, item);
        wss.broadcast(JSON.stringify({
            target: 'reservation',
            type: 'update',
            data: JSON.stringify(item)
        }));
        res.json(item);
    } else {
        res.json(false);
    }
	
	console.log(reservations);
});

app.del('/reservations/:id', function(req, res) {
    console.log('/reservations/' + req.params.id + ': DELETE');	
	var item = findReservation(req.params.id);
    if (item) {
		//console.log('found reservation, removing...');	
		removeFromArray(reservations, item);
        wss.broadcast(JSON.stringify({
            target: 'reservation',
            type: 'delete',
            data: JSON.stringify(item)
        }));
        res.json(true);
    } else {
		//console.log('reservation NOT FOUND...');	
        res.json(false);
    }
});



/*
  PUBLIC API
*/

app.post('/public/register', function(req, res) {
    console.log('/public/register' + ': POST');
    var name = req.body.name;
    var mail = req.body.email;
    var password = req.body.password;
    var studentnumber = req.body.studentnumber;
    var newStudent = {
        name: name,
        password: password,
        email: mail,
        studentnumber: studentnumber
    };

    res.json(addCustomer(JSON.parse(JSON.stringify(newStudent))));
});

app.post('/public/login', function(req, res) {
    console.log('/public/login' + ': POST');
	
    var pwd = req.body.password;
    var mail = req.body.email;

	console.log('got login request for ' + mail);

    var item = findCustomerByEmail(mail);
    if (item != null) {
        if (item.password == pwd) {
            if (!tokens[item.studentnumber]) {
                var token = createGuid();
                tokens[item.studentnumber] = {
                    customerId: item.studentnumber,
                    securityToken: token
                };
            }
            res.json(tokens[item.studentnumber]);
        } else {
            res.json("incorrect password");
        }
    } else {
        res.json("user does not exist");
    }
});


app.get('/public/reservations', checkAuth, function(req, res) {
    console.log('/public/reservations' + ': GET');
    var securityToken = getSecurityToken(req);
    var idCustomer = securityToken.customerId;
    var result = JSON.parse(JSON.stringify(filterReservationsPerCustomer(idCustomer)));

    result.forEach(function(entry) {
        entry.gadget = findGadget(entry.gadgetId);
		// keep miss-spelled name for compatibility with old java admin app:
        entry.watingPosition = getWaitingPositionOfReservation(entry);
		entry.waitingPosition = entry.watingPosition;
        entry.isReady = !isLent(entry.gadgetId) && entry.waitingPosition == 0;

        //console.log(entry.waitingPosition);
        delete entry["gadgetId"];
        delete entry["customerId"];
    });

    res.json(result);
});


app.post('/public/logout', checkAuth, function(req, res) {
    console.log('/public/logout' + ': POST');

    var securityToken = getSecurityToken(req);
    var idCustomer = securityToken.customerId;

    if (tokens[idCustomer]) {
        delete tokens[idCustomer]
        return res.json(true);
    } else {
        return res.json(false);
    }
});



app.get('/public/loans', checkAuth, function(req, res) {
    console.log('/public/loans' + ': GET');
    var securityToken = getSecurityToken(req);
    var idCustomer = securityToken.customerId;
    var result = JSON.parse(JSON.stringify(filterLoansPerCustomer(idCustomer)));

    result.forEach(function(entry) {
        entry.gadget = findGadget(entry.gadgetId);
        delete entry["gadgetId"];
        delete entry["customerId"];
    });

    res.json(result);
});




app.del('/public/reservations', checkAuth, function(req, res) {
    console.log('/public/reservations' + ': DELETE');
    var securityToken = getSecurityToken(req);
    var idCustomer = securityToken.customerId;
    var reservation = findReservation(req.body.id);

    if (reservation) {
        reservation.finished = true;
        wss.broadcast(JSON.stringify({
            target: 'reservation',
            type: 'update',
            data: JSON.stringify(reservation)
        }));
        res.json(true);
    } else {
        res.json(false);
    }
});


app.post('/public/reservations', checkAuth, function(req, res) {
    console.log('/public/reservations' + ': POST');
	console.log(req.body);
	var securityToken = getSecurityToken(req);
    var gadgetId = req.body.gadgetId;
    var idCustomer = securityToken.customerId;
	
	console.log(securityToken);
	
	var reservation = {
		id: createGuid(),
		gadgetId: gadgetId,
		customerId: idCustomer,
		reservationDate: new Date().toJSON(),
		finished: false
	};
	console.log("BEFORE");
	console.log(reservations);
	res.json(addReservation(JSON.parse(JSON.stringify(reservation))));
	console.log("AFTER");
	console.log(reservations);
});


app.get('/public/gadgets', checkAuth, function(req, res) {
	console.log('/public/gadgets' + ': GET');
    res.json(gadgets);
});


function findLoan(id) {
    for (var index = 0; index < loans.length; ++index) {
        var item = loans[index];
        if (item.id === id) {
            return item;
        }
    }
    return null;
}

function isLent(gadgetId) {
    return getLoanBy(gadgetId) != null;
}


function isReservedBy(customerId, gadgetId) {
    return getReservationBy(customerId, gadgetId) != null;
}


function getReservationBy(customerId, gadgetId) {
    for (var index = 0; index < reservations.length; ++index) {
        var item = reservations[index];
        if (item.customerId == customerId && item.gadgetId == gadgetId && !item.finished) {
            return item;
        }
    }
    return null;
}


function getLoanBy(gadgetId) {
    console.log(gadgetId);
    for (var index = 0; index < loans.length; ++index) {
        var item = loans[index];
        console.log(item);
        if (item.gadgetId == gadgetId && item.returnDate == null) {
            return item;
        }
    }
    return null;
}



function findReservation(id) {
    for (var index = 0; index < reservations.length; ++index) {
        var item = reservations[index];
        if (item.id === id) {
            return item;
        }
    }
    return null;
}


function findGadget(inventoryNumber) {
    for (var index = 0; index < gadgets.length; ++index) {
        var item = gadgets[index];
        if (item.inventoryNumber === inventoryNumber) {
            return item;
        }
    }
    return null;
}

function findCustomer(studentnumber) {
    for (var index = 0; index < customers.length; ++index) {
        var item = customers[index];
        if (item.studentnumber == studentnumber) {
            return item;
        }
    }
    return null;
}

function findCustomerByEmail(email) {
    for (var index = 0; index < customers.length; ++index) {
        var item = customers[index];
        if (item.email == email) {
            return item;
        }
    }
    return null;
}

function removeFromArray(array, object) {
    for (var index = 0; index < array.length; ++index) {
        var item = array[index];
        if (item == object) {
			array.splice(index, 1);
            return item;
        }
    }
    return null;
}

function merge(source, target) {
    for (var attrname in source) {
        target[attrname] = source[attrname];
    }
}
