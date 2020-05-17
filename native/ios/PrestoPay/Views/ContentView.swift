import SwiftUI

struct ContentView: View {
  var body: some View {
    LoginView()
  }
}

struct ContentView_Previews: PreviewProvider {
  static var previews: some View {
    ForEach(["iPad Pro (11-inch) (2nd generation)", "iPhone 11", "iPhone SE (2nd generation)"], id: \.self) { device in
      ContentView()
        .previewDisplayName(device)
        .previewDevice(PreviewDevice(rawValue: device))
    }
  }
}
