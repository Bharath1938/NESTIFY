import React from 'react';
import { StatusBar, SafeAreaView, StyleSheet } from 'react-native';
import { RootNavigator } from './src/navigation/RootNavigator';
import { Colors } from './src/theme';

export default function App() {
  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="dark-content" backgroundColor={Colors.background} />
      <RootNavigator />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.background,
  },
});
