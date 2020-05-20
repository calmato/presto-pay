import Foundation
import SwiftUI

extension View {
  func underline() -> some View {
    return VStack {
      self
      Divider()
    }
  }

  func navigationBarColor(_ backgroundColor: Color?) -> some View {
    self.modifier(
      NavigationBarModifier(backgroundColor: backgroundColor)
    )
  }
}

// MARK: NavigationBar extension
struct NavigationBarModifier: ViewModifier {
  var backgroundColor: UIColor?
    
  init(backgroundColor: Color?) {
    self.backgroundColor = backgroundColor?.uiColor()

    // this is not the same as manipulating the proxy directly
    let appearance = UINavigationBarAppearance()
    
    // this overrides everything you have set up earlier.
    appearance.configureWithTransparentBackground()
    appearance.backgroundColor = .clear
    // this only applies to big titles
    appearance.titleTextAttributes = [.foregroundColor: UIColor.white]
    appearance.largeTitleTextAttributes = [.foregroundColor: UIColor.white]
  
    //In the following two lines you make sure that you apply the style for good
    UINavigationBar.appearance().standardAppearance = appearance
    UINavigationBar.appearance().compactAppearance = appearance
    UINavigationBar.appearance().scrollEdgeAppearance = appearance

    // This property is not present on the UINavigationBarAppearance
    // object for some reason and you have to leave it til the end
    UINavigationBar.appearance().tintColor = .white
  }

  func body(content: Content) -> some View {
    ZStack{
      content
      VStack {
        GeometryReader { geometry in
          Color(self.backgroundColor ?? .clear)
            .frame(height: geometry.safeAreaInsets.top)
            .edgesIgnoringSafeArea(.top)
          Spacer()
        }
      }
    }
  }
}
