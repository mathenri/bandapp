//
//  ViewController.swift
//  BandApp
//
//  Created by Mattias Henriksson on 12/25/16.
//  Copyright © 2016 Mattias Henriksson's Apps. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    @IBOutlet var tableView : UITableView!
    
    let eventList = EventList();

    override func viewDidLoad() {
        super.viewDidLoad()
        
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")
        tableView.dataSource = eventList
        
        eventList.addEvent(event: "Test")
        tableView.reloadData()
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

