import SwiftUI

struct ContentView: View {
    let mailButton = UIButton()
    
    var body: some View {
        VStack {
            Button(action: {
                let internationalEmail = "почта-тест@универсальное-принятие-тест.москва"
                var url = "mailto:\(internationalEmail)?subject=my_subject&body=my_body"
                url = url.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
                UIApplication.shared.open(URL(string: url)!)
                
                // Unfortunately, this is the only way to fill programmatically an email
                // on iOS, MFMailComposeViewController  from MessageUI won't work, see:
                // https://stackoverflow.com/questions/69213585/mfmailcomposeviewcontroller-not-displaying-recipients-for-internationalized-emai
             }) {
                 Text("Send Email")
             }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
