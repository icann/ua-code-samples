import Foundation
import IDNA

// an idn domain:
let uLabel = "համընդհանուր-ընկալում-թեստ.հայ"
guard let aLabel = uLabel.idnaEncoded else { exit(1) }
let supportedUrl = "https://" + aLabel
guard let url = URL(string: supportedUrl) else { exit(1) }
let task = URLSession.shared.dataTask(with: url) { data, response, error in
    guard let rawContent = data else { exit(1) }
    guard let content = String(data: rawContent, encoding: String.Encoding.utf8) else { exit(1) }
    if content.contains("UASG Testbed Landing Page") {
        // successfully fetch content of the page
        exit(0)
    } else {
        // error during fetching
        exit(1)
    }
}
task.resume()
RunLoop.main.run()
