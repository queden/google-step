import { Component, AfterViewInit, Output, EventEmitter } from '@angular/core';
import * as scripts from '../../assets/scripts.js';

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

  getDataWrapper = () => {
      console.log("getDataWrapper callled");

      scripts.getData();
  }

}
