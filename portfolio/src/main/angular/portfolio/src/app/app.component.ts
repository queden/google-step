import { Component, ViewChild } from '@angular/core';
import { ServerComponent } from './server/server.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'portfolio';

//   @ViewChild('app-server') child:ServerComponent;

  ngOnInit() {
      console.log("AppComponent Initialized");
  }

//   ServerGetData = () => {
//       console.log("server get data called");
//       this.child.getDataWrapper();
//   }
  
}
