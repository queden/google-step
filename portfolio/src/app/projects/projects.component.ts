import { Component, OnInit } from '@angular/core';
import { ListItem } from '../listItem'

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})
export class ProjectsComponent implements OnInit {
  // initializes empty items object to be initialized with projects
  items
  constructor() { }

  ngOnInit(): void {
      this.items = [
          new ListItem('TalkToEinstein', 'Trying to talk to Albert', 'assets/img/einstein.jpg'),
          new ListItem('Dignify', 'App that provides centralized access to resources for people experiencing homelessness', 'assets/img/dignify.png')
      ];
  }

}
