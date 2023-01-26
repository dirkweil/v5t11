const urlParams = new URLSearchParams(window.location.search);
const bereich = urlParams.get('bereich');
let socket = new WebSocket("ws://" + window.location.host + "/javax.faces.push/stellwerk/" + bereich);
socket.onmessage = function (event) {
  // alert(`[message] Data received from server: ` + JSON.stringify(event.data));
  let drawCommands = JSON.parse(event.data);
  for (const drawCommand of drawCommands) {
    updateHtmlElement(drawCommand);
  }

  updateControlPanel();
};

function updateHtmlElement(drawCommand) {
  console.log('updateHtmlElement: ' + JSON.stringify(drawCommand));
  const htmlElement = document.getElementById(drawCommand.uiId);

  if (htmlElement != null) {
    // console.log('htmlElement: ' + htmlElement);
    const ctx = htmlElement.getContext("2d");

    ctx.lineCap = "butt";
    ctx.lineJoin = "round";

    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.clearRect(0, 0, 1000, 1000);

    drawInaktiv(drawCommand.i, ctx);
    drawAktiv(drawCommand.a, drawCommand.b, ctx);
    drawFahrstrasse(drawCommand.f, drawCommand.z, drawCommand.a, ctx);
    drawSignal(drawCommand.s, drawCommand.l, ctx);
    drawName(drawCommand.n, drawCommand.p, ctx);
  }
}

function drawInaktiv(inaktiveRichtungen, ctx) {
  if (inaktiveRichtungen != null) {
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.beginPath();
    ctx.lineWidth = 200;
    ctx.strokeStyle = "grey";

    for (inaktiveRichtung of inaktiveRichtungen) {
      const start = richtung2endPoint(inaktiveRichtung);
      ctx.moveTo(start.x, start.y);
      ctx.lineTo(500, 500);
    }

    ctx.stroke();
  }
}

function drawAktiv(aktiveRichtungen, besetzt, ctx) {
  if (aktiveRichtungen != null) {
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.beginPath();
    ctx.lineWidth = 300;
    ctx.strokeStyle = besetzt ? "red" : "black";

    const start = richtung2endPoint(aktiveRichtungen[0]);
    const ende = richtung2endPoint(aktiveRichtungen[1]);
    ctx.moveTo(start.x, start.y);
    ctx.lineTo(500, 500);
    ctx.lineTo(ende.x, ende.y);
    ctx.stroke();

    // const xxx = richtung2point(aktiveRichtungen[1]);
    // ctx.beginPath();
    // ctx.lineWidth = 100;
    // ctx.strokeStyle = "lightblue";
    // ctx.moveTo(500, 500);
    // ctx.lineTo(xxx.x, xxx.y);
    // ctx.stroke();

  }
}

function drawFahrstrasse(fahrstasse, zaehlrichtung, aktiveRichtungen, ctx) {
  if (fahrstasse != null) {
    ctx.setTransform(1, 0, 0, 1, 500, 500);
    let richtung = richtung2winkel(aktiveRichtungen[0]) + Math.PI;
    if (richtung > 2 * Math.PI)
      richtung -= 2 * Math.PI;
    richtung += richtung2winkel(aktiveRichtungen[1]);
    richtung /= 2;
    richtung += Math.PI / 2;
    if (zaehlrichtung)
      richtung += Math.PI;
    ctx.rotate(richtung);

    ctx.beginPath();
    ctx.lineWidth = 0;
    ctx.moveTo(220, 0);
    ctx.lineTo(-180, -150);
    ctx.lineTo(-180, 150);
    ctx.closePath();

    switch (fahrstasse) {
      case "Z":
        ctx.fillStyle = "yellow";
        break;

      case "R":
        ctx.fillStyle = "white";
        break;

      default:
        ctx.fillStyle = "magenta";
        break;
    }

    ctx.fill();

  }
}

function drawSignal(position, lichter, ctx) {
  if (position != null) {
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.lineWidth = 40;
    ctx.strokeStyle = "black";
    switch (position) {
      default:
        ctx.translate(95, 175);
        break;
      case 'NO':
        ctx.translate(510, 50);
        break;
      case 'O':
        ctx.translate(825, 95);
        break;
      case 'SO':
        ctx.translate(950, 510);
        break;
      case 'S':
        ctx.translate(905, 825);
        break;
      case 'SW':
        ctx.translate(490, 950);
        break;
      case 'W':
        ctx.translate(175, 905);
        break;
      case 'NW':
        ctx.translate(50, 490);
        break;
    }
    ctx.rotate(richtung2winkel(position));

    // Für jedes Licht einen Kreis zeichnen
    let x = 0;
    for (l of lichter) {
      ctx.beginPath();
      switch (l) {
        case "r":
          ctx.fillStyle = "red";
          break;
        case "g":
          ctx.fillStyle = "lightgreen";
          break;
        case "y":
          ctx.fillStyle = "yellow";
          break;
        case "w":
          ctx.fillStyle = "white";
          break;
        default:
          ctx.fillStyle = "black";
          break;
      }
      ctx.arc(x + 80, 0, 80, 0, 2 * Math.PI);
      ctx.fill();
      ctx.stroke();

      x += 160;
    }

    // Signalfuß zeichnen
    x += 20;
    ctx.beginPath();
    ctx.moveTo(x, -90);
    ctx.lineTo(x, 90);
    ctx.stroke();

  }
}

function drawName(name, position, ctx) {
  if (name != null) {
    const mitte = richtung2namePoint(position);
    ctx.setTransform(1, 0, 0, 1, mitte.x, mitte.y);
    if (name.length > 2) {
      switch (position) {
        case 'W':
        case 'O':
          ctx.rotate(-Math.PI / 2);
          break;
        case 'NW':
        case 'SO':
          ctx.rotate(-Math.PI / 4);
          break;
        case 'NO':
        case 'SW':
          ctx.rotate(Math.PI / 4);
          break;
      }
    }
    ctx.font = "bold 300px Arial";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    ctx.fillStyle = "black";
    ctx.fillText(name, 0, 0);
  }
}

function richtung2endPoint(richtung) {
  switch (richtung) {
    case 'N':
      return {x: 500, y: 0};
    case 'NO':
      return {x: 1000, y: 0};
    case 'O':
      return {x: 1000, y: 500};
    case 'SO':
      return {x: 1000, y: 1000};
    case 'S':
      return {x: 500, y: 1000};
    case 'SW':
      return {x: 0, y: 1000};
    case 'W':
      return {x: 0, y: 500};
    case 'NW':
      return {x: 0, y: 0};
    default:
      return {x: 500, y: 500};
  }
}

function richtung2namePoint(richtung) {
  switch (richtung) {
    case 'N':
      return {x: 500, y: 200};
    case 'NO':
      return {x: 750, y: 250};
    case 'O':
      return {x: 800, y: 500};
    case 'SO':
      return {x: 750, y: 750};
    case 'S':
      return {x: 500, y: 800};
    case 'SW':
      return {x: 250, y: 750};
    case 'W':
      return {x: 200, y: 500};
    case 'NW':
      return {x: 250, y: 250};
    default:
      return {x: 500, y: 500};
  }
}

function richtung2winkel(richtung) {
  switch (richtung) {
    default:
      return 0;
    case 'NO':
      return Math.PI / 4;
    case 'O':
      return Math.PI / 2;
    case 'SO':
      return 3 * Math.PI / 4;
    case 'S':
      return Math.PI;
    case 'SW':
      return 5 * Math.PI / 4;
    case 'W':
      return 6 * Math.PI / 4;
    case 'NW':
      return 7 * Math.PI / 4;
  }

}
