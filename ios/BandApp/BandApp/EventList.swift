//
//  EventList.swift
//  BandApp
//
//  Created by Mattias Henriksson on 12/25/16.
//  Copyright © 2016 Mattias Henriksson's Apps. All rights reserved.
//

import UIKit

class EventList: NSObject {
    
    fileprivate var events: [String] = []
    
    override init() {
        super.init()
        loadItems()
    }
    
    func loadItems() {
        // populate events variable with events from server
    }
    
    func addEvent(event:String) {
        events.append(event)
    }
}

extension EventList: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return events.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
        let event = events[indexPath.row]
        cell.textLabel!.text = event
        return cell
    }
}
