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
    ctx.lineWidth = 30;
    ctx.lineCap = "butt";
    ctx.lineJoin = "round";

    // Zuerst inaktive Segmente zeichnen
    var inaktiveRichtungen = message.i;
    if (inaktiveRichtungen != null) {
      console.log('inaktiveRichtungen: ' + inaktiveRichtungen);

      ctx.beginPath();
      ctx.strokeStyle = "grey";

      for (inaktiveRichtung of inaktiveRichtungen) {
        var start = richtung2point(inaktiveRichtung);
        ctx.moveTo(start.x, start.y);
        ctx.lineTo(50, 50);
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
      ctx.lineTo(50, 50);
      ctx.lineTo(ende.x, ende.y);
      ctx.stroke();
    }

  }

  function richtung2point(richtung) {
    switch (richtung) {
      case 'N':
        return {x: 50, y: 0};
      case 'NO':
        return {x: 100, y: 0};
      case 'O':
        return {x: 100, y: 50};
      case 'SO':
        return {x: 100, y: 100};
      case 'S':
        return {x: 50, y: 100};
      case 'SW':
        return {x: 0, y: 100};
      case 'W':
        return {x: 0, y: 50};
      case 'NW':
        return {x: 0, y: 0};
      default:
        return {x: 50, y: 50};
    }
  }
}
