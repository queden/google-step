import { Component, AfterViewInit, Output, EventEmitter } from '@angular/core';
import * as scripts from '../../assets/scripts.js';
declare var google:any;

@Component({
  selector: 'app-server',
  templateUrl: './server.component.html',
  styleUrls: ['./server.component.css']
})
export class ServerComponent implements AfterViewInit {
  // Simulates body onload event
  @Output() onLoadEvent: EventEmitter<ServerComponent> = new EventEmitter(); 

  constructor() { }

  ngAfterViewInit() {
      // allows parent component to use onloadevent for component to do HTTP GET
      console.log("Server component afterviewinit");
      this.onLoadEvent.emit(this);
  }

  onLoadFunctions = () => {
      this.getDataWrapper();
      this.loadGauge();
  }

  getDataWrapper = () => {
      console.log("getDataWrapper called");

      scripts.getData();
  }

  deleteDataWrapper = () => {
      console.log("deleteDataWrapper called");

      scripts.deleteData();
  }

  loadGauge = () => {
      console.log("loadGauge called");

      google.charts.load('current', {'packages':['gauge']});
      google.charts.setOnLoadCallback(scripts.drawGauge);
  }
}
