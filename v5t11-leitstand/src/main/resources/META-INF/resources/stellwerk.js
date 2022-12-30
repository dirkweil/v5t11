function updateElemente(messages) {
  console.log('updateElemente: ' + JSON.stringify(messages));
  for (const message of messages) {
    updateElement(message);
  }
}

function updateElement(message) {
  console.log('updateElement: ' + JSON.stringify(message));

  var element = document.getElementById(message.uiId);
  if (element != null) {
    // console.log('element: ' + element);

    var ctx = element.getContext("2d");
    ctx.lineCap = "butt";
    ctx.lineJoin = "round";

    ctx.lineWidth = 300;

    ctx.setTransform(1, 0, 0, 1, 0, 0);

    // Zuerst inaktive Segmente zeichnen
    var inaktiveRichtungen = message.i;
    if (inaktiveRichtungen != null) {
      console.log('inaktiveRichtungen: ' + inaktiveRichtungen);

      ctx.beginPath();
      ctx.strokeStyle = "grey";

      for (inaktiveRichtung of inaktiveRichtungen) {
        var start = richtung2point(inaktiveRichtung);
        ctx.moveTo(start.x, start.y);
        ctx.lineTo(500, 500);
      }

      ctx.stroke();
    }

    // Dann aktive Segmente zeichnen
    var aktiveRichtungen = message.a;
    if (aktiveRichtungen != null) {
      console.log('aktiveRichtungen: ' + aktiveRichtungen);

      ctx.beginPath();
      ctx.strokeStyle = message.b ? "red" : "black";

      var start = richtung2point(aktiveRichtungen[0]);
      var ende = richtung2point(aktiveRichtungen[1]);
      ctx.moveTo(start.x, start.y);
      ctx.lineTo(500, 500);
      ctx.lineTo(ende.x, ende.y);
      ctx.stroke();
    }

    if (message.s != null) {
      ctx.lineWidth = 40;
      ctx.strokeStyle = "black";

      switch (message.p) {
        case 'N':
          ctx.translate(95, 175);
          break;
        case 'NO':
          ctx.translate(510, 50);
          ctx.rotate(Math.PI / 4);
          break;
        case 'O':
          ctx.translate(825, 95);
          ctx.rotate(Math.PI / 2);
          break;
        case 'SO':
          ctx.translate(950, 510);
          ctx.rotate(3 * Math.PI / 4);
          break;
        case 'S':
          ctx.translate(905, 825);
          ctx.rotate(Math.PI);
          break;
        case 'SW':
          ctx.translate(490, 950);
          ctx.rotate(5 * Math.PI / 4);
          break;
        case 'W':
          ctx.translate(175, 905);
          ctx.rotate(6 * Math.PI / 4);
          break;
        case 'NW':
          ctx.translate(50, 490);
          ctx.rotate(7 * Math.PI / 4);
          break;
      }

      // Für jede Farbe einen Kreis zeichnen
      var x = 0;
      for (f of message.f) {
        ctx.beginPath();
        ctx.fillStyle = f2Color(f);
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

  function richtung2point(richtung) {
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

  function f2Color(f) {
    switch (f) {
      case "r":
        return "red";
      case "g":
        return "lightgreen";
      case "y":
        return "yellow";
      case "w":
        return "white";
      default:
        return "black";
    }
  }
}
