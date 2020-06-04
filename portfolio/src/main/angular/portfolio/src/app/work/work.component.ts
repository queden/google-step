import { Component, OnInit } from '@angular/core';
import { ListItem } from '../listItem'

@Component({
  selector: 'app-work',
  templateUrl: './work.component.html',
  styleUrls: ['./work.component.css']
})
export class WorkComponent implements OnInit {
  // initializes empty items object to be initialized with work experience
  items
  constructor() { }

  ngOnInit(): void {
      // populates items list with work experience
      this.items = [
          new ListItem('Google', 'May 2020 - Present', 'Edge TPU Team', 'assets/img/goog.png'),
          new ListItem('MLevel', 'January 2019 - August 2019', 'Software Development Intern', 'assets/img/mlevel.png')
      ];
  }

}
